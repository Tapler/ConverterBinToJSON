package com.seroev.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DataOrder {
    private int tagType;
    private byte[] value;
}
