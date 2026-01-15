package com.remembervelocity;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private final Map<String, String> messages = new HashMap<>();

    public Lang(Map<String, Object> raw) {
        raw.forEach((key, value) -> messages.put(key, String.valueOf(value)));
    }

    public String get(String key) {
        return messages.getOrDefault(key, key);
    }
}
