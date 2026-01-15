package com.remembervelocity;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoader {
    private final Path dataDirectory;

    public ConfigLoader(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public Config loadConfig() throws IOException {
        Files.createDirectories(dataDirectory);
        Path configPath = dataDirectory.resolve("config.yml");
        if (Files.notExists(configPath)) {
            copyResource("config.yml", configPath);
        }
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Files.newInputStream(configPath)) {
            Map<String, Object> raw = yaml.load(inputStream);
            Config config = ConfigParser.parse(raw);
            config.validate();
            return config;
        }
    }

    public Lang loadLang(String langFile) throws IOException {
        Path langPath = dataDirectory.resolve(langFile);
        if (Files.notExists(langPath)) {
            Files.createDirectories(langPath.getParent());
            copyResource(langFile, langPath);
        }
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Files.newInputStream(langPath)) {
            Map<String, Object> raw = yaml.load(inputStream);
            return new Lang(raw);
        }
    }

    private void copyResource(String resourceName, Path target) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourceName);
            }
            Files.copy(inputStream, target);
        }
    }
}
