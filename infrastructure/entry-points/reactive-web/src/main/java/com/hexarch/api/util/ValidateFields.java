package com.hexarch.api.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ValidateFields {
    public static void validateFields(Object object) {
        if (object == null) {
            throw new RuntimeException("Object is null");
        }

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(object);
                isNullOrEmpty(value, field.getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }
    }

    private static void isNullOrEmpty(Object value, String fieldName) {
        if (value == null) {
            throw new RuntimeException("Field '" + fieldName + "' is null");
        }

        if (value instanceof String str && str.trim().isEmpty()) {
            throw new RuntimeException("Field '" + fieldName + "' is empty");
        }
    }
}
