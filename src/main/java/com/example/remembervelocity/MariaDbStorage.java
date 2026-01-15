package com.example.remembervelocity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class MariaDbStorage implements Storage {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String tableName;

    public MariaDbStorage(Config.MariaDbConfig config) {
        this.jdbcUrl = String.format(
                "jdbc:mariadb://%s:%d/%s?useSSL=%s",
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.isUseSsl()
        );
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.tableName = config.getTable();
        initializeTable();
    }

    @Override
    public Optional<String> getLastServer(UUID uuid) {
        String sql = "SELECT server_name FROM " + tableName + " WHERE uuid = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("server_name"));
                }
            }
        } catch (SQLException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public void setLastServer(UUID uuid, String serverName) {
        String sql = "INSERT INTO " + tableName + " (uuid, server_name) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE server_name = VALUES(server_name)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, serverName);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "uuid VARCHAR(36) PRIMARY KEY, "
                + "server_name VARCHAR(255) NOT NULL, "
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                + ")";
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
