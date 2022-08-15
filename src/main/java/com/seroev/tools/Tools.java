package com.seroev.tools;

import static org.apache.commons.lang3.ArrayUtils.reverse;

public class Tools {
    public static long convertToLong(byte[] bytes, boolean needReverse) {
        long value = 0;
        // Для типов данных с little endian необходимо развернуть значение
        if (needReverse) {
            reverse(bytes);
        }
        for (byte b : bytes) {
            // Сдвиг предыдущего значения на 8 бит вправо и добавление следующего значения
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

    public static int convertToInt(byte[] bytes, boolean needReverse) {
        int value = 0;
        // Для типов данных с little endian необходимо развернуть значение
        if (needReverse) {
            reverse(bytes);
        }
        for (byte b : bytes) {
            // Сдвиг предыдущего значения на 8 бит вправо и добавление следующего значения
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }
}
