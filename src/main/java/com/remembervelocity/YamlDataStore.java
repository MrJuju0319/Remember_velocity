package com.remembervelocity;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class YamlDataStore implements DataStore {
    private final Path file;
    private final boolean flushOnUpdate;
    private final Map<UUID, String> cache = new LinkedHashMap<>();
    private final Yaml yaml;

    public YamlDataStore(Path file, boolean flushOnUpdate) throws IOException {
        this.file = file;
        this.flushOnUpdate = flushOnUpdate;
        Files.createDirectories(file.getParent());
        if (Files.exists(file)) {
            load();
        }
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);
        this.yaml = new Yaml(options);
    }

    @Override
    public synchronized Optional<String> getLastServer(UUID playerId) {
        return Optional.ofNullable(cache.get(playerId));
    }

    @Override
    public synchronized void setLastServer(UUID playerId, String serverName) {
        cache.put(playerId, serverName);
        if (flushOnUpdate) {
            save();
        }
    }

    private void load() throws IOException {
        Yaml yamlLoader = new Yaml();
        try (InputStream inputStream = Files.newInputStream(file)) {
            Object data = yamlLoader.load(inputStream);
            if (data instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    UUID uuid = UUID.fromString(String.valueOf(entry.getKey()));
                    cache.put(uuid, String.valueOf(entry.getValue()));
                }
            }
        }
    }

    private synchronized void save() {
        Map<String, String> output = new LinkedHashMap<>();
        cache.forEach((uuid, server) -> output.put(uuid.toString(), server));
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
            yaml.dump(output, writer);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
        if (!flushOnUpdate) {
            save();
        }
    }
}
