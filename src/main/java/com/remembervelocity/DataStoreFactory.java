package com.remembervelocity;

import java.io.IOException;
import java.nio.file.Path;

public class DataStoreFactory {
    private DataStoreFactory() {
    }

    public static DataStore create(Config config, Path dataDirectory) throws IOException {
        String type = config.getStorage().getType();
        if (type.equalsIgnoreCase("mariadb")) {
            return new MariaDbDataStore(config.getStorage().getMariadb());
        }
        Config.Yaml yaml = config.getStorage().getYaml();
        Path file = dataDirectory.resolve(yaml.getFile());
        return new YamlDataStore(file, yaml.isFlushOnUpdate());
    }
}
