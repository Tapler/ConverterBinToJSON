package com.seroev.converter;

import com.seroev.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Converter.class);

    public static Serializable convertByTag(byte[] bytes, int maxLength, int tag) {
        if (bytes.length > maxLength) {
            String message = String.format("Превышена максимальная длина: %d ", maxLength);
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
        switch (tag) {
            case 1: {
                int seconds = Tools.convertToInt(bytes, true);
                return seconds * 1000L;
            }
            case 2:
            case 12:
            case 14: {
                return Tools.convertToLong(bytes, true);
            }
            case 3:
            case 11: {
                String result;
                try {
                    result = new String(bytes, "CP866");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(String.format("Не удалось получить строку для tag= %d", tag), e);
                    return null;
                }
                return result;
            }
            case 13: {
                return Tools.convertToLong(bytes, false) / Math.pow(10, bytes[0]);
            }
            default: {
                String errorMessage = "Данный тип не поддерживается: " + tag;
                LOGGER.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

        }
    }
}
