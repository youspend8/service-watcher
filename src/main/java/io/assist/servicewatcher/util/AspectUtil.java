package io.assist.servicewatcher.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AspectUtil {
    public static MethodSignature getMethodSignature(JoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    public static Method getMethod(JoinPoint joinPoint) {
        return getMethodSignature(joinPoint).getMethod();
    }

    public static List<Annotation> getAnnotations(Method method) {
        return Arrays.asList(method.getAnnotations());
    }

    public static boolean isAnnotation(Method method, Annotation annotation) {
        return getAnnotations(method)
                .stream()
                .anyMatch(item -> item.annotationType().isInstance(annotation));
    }
}
