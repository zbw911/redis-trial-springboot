package com.example.utils;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangbaowei
 * Create  on 2020/7/7 18:38.
 */
public class EnvUtils {
    public static boolean hasProperty(Environment environment, String propertyName) {
        String val = environment.getProperty(propertyName);
        boolean result = val != null && !val.trim().isEmpty();
        if (result) {
            return true;
        }

        Class<? extends HashMap> aClass = (new HashMap<>()).getClass();

        Map<String, Object> subProperties = getBindResult(environment, propertyName, aClass);

        return subProperties != null && subProperties.size() > 0;
    }

    public static <T> T getBindResult(Environment environment, String propertyName, Class<T> target) {
        Binder binder = Binder.get(environment);
        T subProperties = binder.bind(propertyName, target).orElse(null);

        return subProperties;
    }
}
