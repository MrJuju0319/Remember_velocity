package com.remembervelocity;

import java.util.Map;

public final class MessageFormatter {
    private MessageFormatter() {
    }

    public static String format(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result.replace('&', '§');
    }
}
