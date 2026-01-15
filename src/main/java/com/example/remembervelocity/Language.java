package com.example.remembervelocity;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Language {
    private final Map<String, String> messages;

    private Language(Map<String, String> messages) {
        this.messages = messages;
    }

    public static Language load(Path path) {
        if (!Files.exists(path)) {
            return new Language(Collections.emptyMap());
        }
        Yaml yaml = new Yaml();
        try (Reader reader = Files.newBufferedReader(path)) {
            Object loaded = yaml.load(reader);
            if (loaded instanceof Map<?, ?> map) {
                Map<String, String> output = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        output.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                }
                return new Language(output);
            }
        } catch (IOException ignored) {
        }
        return new Language(Collections.emptyMap());
    }

    public String message(String key) {
        return messages.getOrDefault(key, key);
    }

    public String message(String key, Map<String, String> placeholders) {
        String message = message(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
