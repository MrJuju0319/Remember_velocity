package com.remembervelocity;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class MariaDbDataStore implements DataStore {
    private final HikariDataSource dataSource;
    private final String table;

    public MariaDbDataStore(Config.MariaDb config) {
        this.table = config.getTable();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format(
                "jdbc:mariadb://%s:%d/%s?useSSL=%s",
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.isUseSsl()));
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setConnectionTimeout(config.getConnectionTimeoutMs());
        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
        this.dataSource = new HikariDataSource(hikariConfig);
        if (config.isCreateTable()) {
            createTable();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " (uuid VARCHAR(36) PRIMARY KEY, server VARCHAR(128) NOT NULL)";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
    }

    @Override
    public Optional<String> getLastServer(UUID playerId) {
        String sql = "SELECT server FROM " + table + " WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.ofNullable(resultSet.getString("server"));
                }
            }
        } catch (SQLException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public void setLastServer(UUID playerId, String serverName) {
        String sql = "INSERT INTO " + table + " (uuid, server) VALUES (?, ?) ON DUPLICATE KEY UPDATE server = VALUES(server)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());
            statement.setString(2, serverName);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
