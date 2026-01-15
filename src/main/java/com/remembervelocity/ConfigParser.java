package com.remembervelocity;

import java.util.Map;

public final class ConfigParser {
    private ConfigParser() {
    }

    @SuppressWarnings("unchecked")
    public static Config parse(Map<String, Object> raw) {
        Config config = new Config();
        config.setDebug(getBoolean(raw, "debug", false));
        config.setSpawnServer(getString(raw, "spawn-server", "spawn"));
        config.setLangFile(getString(raw, "lang-file", "lang/messages.yml"));

        Map<String, Object> storageRaw = getMap(raw, "storage");
        Config.Storage storage = new Config.Storage();
        storage.setType(getString(storageRaw, "type", "yaml"));

        Map<String, Object> yamlRaw = getMap(storageRaw, "yaml");
        Config.Yaml yaml = new Config.Yaml();
        yaml.setFile(getString(yamlRaw, "file", "players.yml"));
        yaml.setFlushOnUpdate(getBoolean(yamlRaw, "flush-on-update", true));
        storage.setYaml(yaml);

        Map<String, Object> mariadbRaw = getMap(storageRaw, "mariadb");
        Config.MariaDb mariaDb = new Config.MariaDb();
        mariaDb.setHost(getString(mariadbRaw, "host", "127.0.0.1"));
        mariaDb.setPort(getInt(mariadbRaw, "port", 3306));
        mariaDb.setDatabase(getString(mariadbRaw, "database", "velocity"));
        mariaDb.setUsername(getString(mariadbRaw, "username", "velocity"));
        mariaDb.setPassword(getString(mariadbRaw, "password", "change-me"));
        mariaDb.setTable(getString(mariadbRaw, "table", "remember_velocity_players"));
        mariaDb.setUseSsl(getBoolean(mariadbRaw, "use-ssl", false));
        mariaDb.setConnectionTimeoutMs(getInt(mariadbRaw, "connection-timeout-ms", 5000));
        mariaDb.setMaximumPoolSize(getInt(mariadbRaw, "maximum-pool-size", 10));
        mariaDb.setCreateTable(getBoolean(mariadbRaw, "create-table", true));
        storage.setMariadb(mariaDb);
        config.setStorage(storage);

        Map<String, Object> recordingRaw = getMap(raw, "recording");
        Config.Recording recording = new Config.Recording();
        recording.setOnServerConnect(getBoolean(recordingRaw, "on-server-connect", true));
        config.setRecording(recording);

        return config;
    }

    private static String getString(Map<String, Object> raw, String key, String fallback) {
        Object value = raw.get(key);
        if (value == null) {
            return fallback;
        }
        return String.valueOf(value);
    }

    private static boolean getBoolean(Map<String, Object> raw, String key, boolean fallback) {
        Object value = raw.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static int getInt(Map<String, Object> raw, String key, int fallback) {
        Object value = raw.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }
}
