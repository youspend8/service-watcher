package io.assist.servicewatcher.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ConvertUtil {
    public static Map<String, Object> objectAsMap(Object object) {
        return new ObjectMapper().convertValue(object, new TypeReference<Map<String, Object>>() {});
    }
}
