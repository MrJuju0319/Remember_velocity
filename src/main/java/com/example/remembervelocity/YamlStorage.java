package com.example.remembervelocity;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlStorage implements Storage {
    private final Path filePath;
    private final Map<UUID, String> players = new ConcurrentHashMap<>();
    private final Yaml yaml;

    public YamlStorage(Path filePath) {
        this.filePath = filePath;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        this.yaml = new Yaml(options);
        load();
    }

    @Override
    public Optional<String> getLastServer(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

    @Override
    public synchronized void setLastServer(UUID uuid, String serverName) {
        players.put(uuid, serverName);
        save();
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(filePath)) {
            Object loaded = yaml.load(reader);
            if (loaded instanceof Map<?, ?> map) {
                Map<UUID, String> temp = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() == null || entry.getValue() == null) {
                        continue;
                    }
                    UUID uuid = UUID.fromString(entry.getKey().toString());
                    temp.put(uuid, entry.getValue().toString());
                }
                players.clear();
                players.putAll(temp);
            }
        } catch (IOException | IllegalArgumentException ignored) {
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException ignored) {
            return;
        }
        Map<String, String> output = new HashMap<>();
        for (Map.Entry<UUID, String> entry : players.entrySet()) {
            output.put(entry.getKey().toString(), entry.getValue());
        }
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            yaml.dump(output, writer);
        } catch (IOException ignored) {
        }
    }
}
