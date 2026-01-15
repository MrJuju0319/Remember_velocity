package com.remembervelocity;

import java.util.Objects;

public class Config {
    private boolean debug;
    private String spawnServer;
    private String langFile;
    private Storage storage;
    private Recording recording;

    public boolean isDebug() {
        return debug;
    }

    public String getSpawnServer() {
        return spawnServer;
    }

    public String getLangFile() {
        return langFile;
    }

    public Storage getStorage() {
        return storage;
    }

    public Recording getRecording() {
        return recording;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setSpawnServer(String spawnServer) {
        this.spawnServer = spawnServer;
    }

    public void setLangFile(String langFile) {
        this.langFile = langFile;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public void validate() {
        Objects.requireNonNull(spawnServer, "spawn-server is required");
        Objects.requireNonNull(langFile, "lang-file is required");
        Objects.requireNonNull(storage, "storage is required");
        Objects.requireNonNull(recording, "recording is required");
        storage.validate();
        recording.validate();
    }

    public static class Storage {
        private String type;
        private Yaml yaml;
        private MariaDb mariadb;

        public String getType() {
            return type;
        }

        public Yaml getYaml() {
            return yaml;
        }

        public MariaDb getMariadb() {
            return mariadb;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setYaml(Yaml yaml) {
            this.yaml = yaml;
        }

        public void setMariadb(MariaDb mariadb) {
            this.mariadb = mariadb;
        }

        public void validate() {
            Objects.requireNonNull(type, "storage.type is required");
            if (type.equalsIgnoreCase("yaml")) {
                Objects.requireNonNull(yaml, "storage.yaml is required");
                yaml.validate();
            } else if (type.equalsIgnoreCase("mariadb")) {
                Objects.requireNonNull(mariadb, "storage.mariadb is required");
                mariadb.validate();
            }
        }
    }

    public static class Yaml {
        private String file;
        private boolean flushOnUpdate;

        public String getFile() {
            return file;
        }

        public boolean isFlushOnUpdate() {
            return flushOnUpdate;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public void setFlushOnUpdate(boolean flushOnUpdate) {
            this.flushOnUpdate = flushOnUpdate;
        }

        public void validate() {
            Objects.requireNonNull(file, "storage.yaml.file is required");
        }
    }

    public static class MariaDb {
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
        private String table;
        private boolean useSsl;
        private int connectionTimeoutMs;
        private int maximumPoolSize;
        private boolean createTable;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getTable() {
            return table;
        }

        public boolean isUseSsl() {
            return useSsl;
        }

        public int getConnectionTimeoutMs() {
            return connectionTimeoutMs;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public boolean isCreateTable() {
            return createTable;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public void setUseSsl(boolean useSsl) {
            this.useSsl = useSsl;
        }

        public void setConnectionTimeoutMs(int connectionTimeoutMs) {
            this.connectionTimeoutMs = connectionTimeoutMs;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public void setCreateTable(boolean createTable) {
            this.createTable = createTable;
        }

        public void validate() {
            Objects.requireNonNull(host, "storage.mariadb.host is required");
            Objects.requireNonNull(database, "storage.mariadb.database is required");
            Objects.requireNonNull(username, "storage.mariadb.username is required");
            Objects.requireNonNull(password, "storage.mariadb.password is required");
            Objects.requireNonNull(table, "storage.mariadb.table is required");
        }
    }

    public static class Recording {
        private boolean onServerConnect;

        public boolean isOnServerConnect() {
            return onServerConnect;
        }

        public void setOnServerConnect(boolean onServerConnect) {
            this.onServerConnect = onServerConnect;
        }

        public void validate() {
            // no required fields yet
        }
    }
}
