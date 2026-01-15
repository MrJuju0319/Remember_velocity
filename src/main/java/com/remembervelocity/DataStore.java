package com.remembervelocity;

import java.util.Optional;
import java.util.UUID;

public interface DataStore extends AutoCloseable {
    Optional<String> getLastServer(UUID playerId);

    void setLastServer(UUID playerId, String serverName);

    @Override
    void close();
}
