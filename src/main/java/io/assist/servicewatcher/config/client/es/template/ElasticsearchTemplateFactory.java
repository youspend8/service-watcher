package io.assist.servicewatcher.config.client.es.template;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static io.assist.servicewatcher.util.ReflectionUtil.isDate;
import static io.assist.servicewatcher.util.ReflectionUtil.isPrimitiveOrWrapper;

public class ElasticsearchTemplateFactory {
    public static XContentBuilder createWithReflection(Class<?> clazz) {
        try {
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            xContentBuilder.startObject();
            xContentBuilder.startObject("properties");
            reflectClass(xContentBuilder, clazz);
            xContentBuilder.endObject();
            xContentBuilder.endObject();
            return xContentBuilder;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void textWithKeyword(XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.field("type", "text");
        xContentBuilder.startObject("fields");
        xContentBuilder.startObject("keyword");
        xContentBuilder.field("type", "keyword");
        xContentBuilder.field("ignore_above", 256);
        xContentBuilder.endObject();
        xContentBuilder.endObject();
    }

    private static void reflectClass(XContentBuilder xContentBuilder, Class<?> clazz) throws IOException {
        for (Field field : clazz.getDeclaredFields()) {
            xContentBuilder.startObject((field.getName().contains("timestamp") ? "@" : "") + field.getName());
            if (field.getType().isEnum() || field.getType() == String.class) {
                textWithKeyword(xContentBuilder);
            } else if (isPrimitiveOrWrapper(field)) {
                xContentBuilder.field("type", asMappingTypeName(field.getType().getTypeName().toLowerCase(Locale.ROOT)));
            } else if (isDate(field)) {
                xContentBuilder.field("type", "date");
            } else {
                xContentBuilder.startObject("properties");
                reflectClass(xContentBuilder, field.getType());
                xContentBuilder.endObject();
            }
            xContentBuilder.endObject();
        }
    }

    private static String asMappingTypeName(String primitiveTypeName) {
        String substrName = primitiveTypeName.substring(primitiveTypeName.lastIndexOf(".") + 1);
        Map<String, String> map = new HashMap<>();
        map.put("char", "text");
        map.put("int", "integer");
        return map.getOrDefault(substrName, substrName);
    }
}
