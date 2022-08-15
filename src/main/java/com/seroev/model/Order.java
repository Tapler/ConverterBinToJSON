package com.seroev.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    @JsonIgnore
    private long datetime;
    @JsonProperty(value = "orderNumber", index = 2)
    private long orderNumber;
    @JsonProperty(value = "customerName", index = 3)
    private String customerName;
    @JsonProperty(value = "items", index = 4)
    private List<OrderItem> orderItems;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(value = "datetime", index = 1)
    public Date getDateTimeAsDate() {
        return new Date(datetime);
    }
}
