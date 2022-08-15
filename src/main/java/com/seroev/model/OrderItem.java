package com.seroev.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderItem {
    @JsonProperty(value = "name", index = 1)
    private String name;
    @JsonProperty(value = "price", index = 2)
    private long price;
    @JsonProperty(value = "quantity", index = 3)
    private double quantity;
    @JsonProperty(value = "sum", index = 4)
    private long sum;
}
