package com.seroev.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seroev.converter.Converter;
import com.seroev.model.DataOrder;
import com.seroev.model.Order;
import com.seroev.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.seroev.enums.TagTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    public String getJsonFromBytes(byte[] bytes) {
        Order order = parse(bytes);
        String result = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("Не удалось записать заказ: %s", Objects.requireNonNull(order).toString()), e);
            e.printStackTrace();
        }
        return result;
    }

    private Order parse(byte[] bytes) {
        if (bytes.length < 4) {
            return null;
        }
        List<DataOrder> orderDataList = getDataFromBytes(bytes);
        Order order = getOrder(orderDataList);
        return order;
    }

    private Order getOrder(List<DataOrder> orderDataList) {
        Order order = new Order();

        for (DataOrder orderData : orderDataList) {
            int tagType = orderData.getTagType();
            switch (tagType) {
                case 1: {
                    int maxLength = TagTypes.DATE_TIME.getMaxLength();
                    Long value = (Long) Converter.convertByTag(orderData.getValue(), maxLength, tagType);
                    order.setDatetime(value);
                    break;
                }
                case 2: {
                    int maxLength = TagTypes.ORDER_NUMBER.getMaxLength();
                    Long value = (Long) Converter.convertByTag(orderData.getValue(), maxLength, tagType);
                    order.setOrderNumber(value);
                    break;
                }
                case 3: {
                    int maxLength = TagTypes.CUSTOMER_NAME.getMaxLength();
                    String value = (String) Converter.convertByTag(orderData.getValue(), maxLength, tagType);
                    order.setCustomerName(value);
                    break;
                }
                case 4: {
                    List<DataOrder> itemDataList = getDataFromBytes(orderData.getValue());
                    List<OrderItem> orderItemList = getOrderItems(itemDataList);
                    order.setOrderItems(orderItemList);
                    break;
                }
                default: {
                    String message = String.format("Данный тип не поддерживается: %s ", orderData);
                    LOGGER.error(message);
                    throw new IllegalArgumentException(message);
                }
            }
        }
        LOGGER.info("Получили заказ:" + order);
        return order;
    }

    private List<OrderItem> getOrderItems(List<DataOrder> itemDataList) {

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();

        for (DataOrder itemData : itemDataList) {
            int tagType = itemData.getTagType();
            switch (tagType) {
                case 11: {
                    int maxLength = TagTypes.NAME.getMaxLength();
                    String value = (String) Converter.convertByTag(itemData.getValue(), maxLength, tagType);
                    orderItem.setName(value);
                    break;
                }
                case 12: {
                    int maxLength = TagTypes.PRICE.getMaxLength();
                    Long value = (Long) Converter.convertByTag(itemData.getValue(), maxLength, tagType);
                    orderItem.setPrice(value);
                    break;
                }
                case 13: {
                    int maxLength = TagTypes.QUANTITY.getMaxLength();
                    Double value = (Double) Converter.convertByTag(itemData.getValue(), maxLength, tagType);
                    orderItem.setQuantity(value);
                    break;
                }
                case 14: {
                    int maxLength = TagTypes.SUM.getMaxLength();
                    Long value = (Long) Converter.convertByTag(itemData.getValue(), maxLength, tagType);
                    if (orderItem.getPrice() * orderItem.getQuantity() != value) {
                        LOGGER.error(String.format("Задана неверная сумма: %s для тэга: %s", value, tagType));
                        throw new IllegalArgumentException();
                    }
                    orderItem.setSum(value);
                    break;
                }
                default: {
                    String message = String.format("Данный тип не поддерживается: %s ", itemData.toString());
                    LOGGER.error(message);
                    throw new IllegalArgumentException(message);
                }
            }
        }
        orderItems.add(orderItem);
        return orderItems;
    }

    private List<DataOrder> getDataFromBytes(byte[] bytes) {
        List<DataOrder> dataList = new ArrayList<>();
        while (bytes.length > 4) {
            int type = getValueFromTwoBytes(bytes[1], bytes[0]);
            int length = getValueFromTwoBytes(bytes[3], bytes[2]);

            DataOrder data = new DataOrder(type, Arrays.copyOfRange(bytes, 4, 4 + length));

            bytes = Arrays.copyOfRange(bytes, 4 + length, bytes.length);
            dataList.add(data);
        }
        return dataList;
    }

    private int getValueFromTwoBytes(byte high, byte low) {
        return ((0xFF & (int) high) * 256) + ((0xFF & (int) low));
    }
}
