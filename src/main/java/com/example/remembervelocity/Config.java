package com.example.remembervelocity;

import java.util.Objects;

public class Config {
    private String spawnServer = "spawn";
    private String languageFile = "lang.yml";
    private boolean useLastServer = true;
    private boolean saveOnPostConnect = true;
    private boolean saveOnDisconnect = true;
    private StorageConfig storage = new StorageConfig();

    public String getSpawnServer() {
        return spawnServer;
    }

    public void setSpawnServer(String spawnServer) {
        this.spawnServer = Objects.requireNonNullElse(spawnServer, "spawn");
    }

    public String getLanguageFile() {
        return languageFile;
    }

    public void setLanguageFile(String languageFile) {
        this.languageFile = Objects.requireNonNullElse(languageFile, "lang.yml");
    }

    public boolean isUseLastServer() {
        return useLastServer;
    }

    public void setUseLastServer(boolean useLastServer) {
        this.useLastServer = useLastServer;
    }

    public boolean isSaveOnPostConnect() {
        return saveOnPostConnect;
    }

    public void setSaveOnPostConnect(boolean saveOnPostConnect) {
        this.saveOnPostConnect = saveOnPostConnect;
    }

    public boolean isSaveOnDisconnect() {
        return saveOnDisconnect;
    }

    public void setSaveOnDisconnect(boolean saveOnDisconnect) {
        this.saveOnDisconnect = saveOnDisconnect;
    }

    public StorageConfig getStorage() {
        return storage;
    }

    public void setStorage(StorageConfig storage) {
        this.storage = Objects.requireNonNullElseGet(storage, StorageConfig::new);
    }

    public static class StorageConfig {
        private String type = "yaml";
        private YamlConfig yaml = new YamlConfig();
        private MariaDbConfig mariadb = new MariaDbConfig();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = Objects.requireNonNullElse(type, "yaml");
        }

        public YamlConfig getYaml() {
            return yaml;
        }

        public void setYaml(YamlConfig yaml) {
            this.yaml = Objects.requireNonNullElseGet(yaml, YamlConfig::new);
        }

        public MariaDbConfig getMariadb() {
            return mariadb;
        }

        public void setMariadb(MariaDbConfig mariadb) {
            this.mariadb = Objects.requireNonNullElseGet(mariadb, MariaDbConfig::new);
        }
    }

    public static class YamlConfig {
        private String file = "players.yml";

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = Objects.requireNonNullElse(file, "players.yml");
        }
    }

    public static class MariaDbConfig {
        private String host = "localhost";
        private int port = 3306;
        private String database = "velocity";
        private String username = "velocity";
        private String password = "password";
        private String table = "player_last_server";
        private boolean useSsl = false;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = Objects.requireNonNullElse(host, "localhost");
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = Objects.requireNonNullElse(database, "velocity");
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = Objects.requireNonNullElse(username, "velocity");
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = Objects.requireNonNullElse(password, "password");
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = Objects.requireNonNullElse(table, "player_last_server");
        }

        public boolean isUseSsl() {
            return useSsl;
        }

        public void setUseSsl(boolean useSsl) {
            this.useSsl = useSsl;
        }
    }
}
