package com.seroev.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagTypes {
    DATE_TIME(1, 4),
    ORDER_NUMBER(2, 8),
    CUSTOMER_NAME(3, 1000),
    NAME(11, 200),
    PRICE(12, 6),
    QUANTITY(13, 8),
    SUM(14, 6);

    private final int tag;
    private final int maxLength;
}
