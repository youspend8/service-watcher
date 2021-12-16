package io.assist.servicewatcher.util;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReflectionUtil {
    public static boolean isDate(Field field) {
        List<?> types = Arrays.asList(
                LocalDateTime.class,
                LocalDate.class,
                Timestamp.class,
                Date.class);
        return types.contains(field.getType());
    }

    public static boolean isPrimitiveOrWrapper(Field field) {
        List<?> types = Arrays.asList(
                boolean.class, Boolean.class,
                char.class, Character.class,
                double.class, Double.class,
                float.class, Float.class,
                int.class, Integer.class,
                long.class, Long.class,
                short.class, Short.class);
        return types.contains(field.getType());
    }
}
