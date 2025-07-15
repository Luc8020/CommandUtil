package com.util.command.core;

import java.util.HashMap;
import java.util.Map;

public class InstanceRegistry {
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public Object getOrCreate(Class<?> clazz) {
        return instances.computeIfAbsent(clazz, c -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance for class: " + clazz.getName(), e);
            }
        });
    }
}