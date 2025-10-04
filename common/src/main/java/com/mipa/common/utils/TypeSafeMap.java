package com.mipa.common.utils;

import java.util.HashMap;
import java.util.Map;

public class TypeSafeMap {

    private final Map<Class<?>, Object> map = new HashMap<>();

    public <T> void put(Class<T> type, T value) {
        map.put(type, value);
    }

    public <T> T get(Class<T> type) {
        return type.cast(map.get(type));
    }

}
