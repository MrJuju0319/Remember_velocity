package com.example.remembervelocity;

import java.io.Closeable;
import java.util.Optional;
import java.util.UUID;

public interface Storage extends Closeable {
    Optional<String> getLastServer(UUID uuid);

    void setLastServer(UUID uuid, String serverName);

    @Override
    default void close() {
    }
}
