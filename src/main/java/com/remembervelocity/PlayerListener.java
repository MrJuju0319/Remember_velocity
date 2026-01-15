package com.remembervelocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;

public class PlayerListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Config config;
    private final Lang lang;
    private final DataStore dataStore;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    public PlayerListener(ProxyServer proxy, Logger logger, Config config, Lang lang, DataStore dataStore) {
        this.proxy = proxy;
        this.logger = logger;
        this.config = config;
        this.lang = lang;
        this.dataStore = dataStore;
    }

    @Subscribe
    public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        Optional<String> lastServer = dataStore.getLastServer(player.getUniqueId());

        if (lastServer.isPresent()) {
            String serverName = lastServer.get();
            Optional<RegisteredServer> server = proxy.getServer(serverName);
            if (server.isPresent()) {
                event.setInitialServer(server.get());
                debug("log-known-record", Map.of(
                        "player", player.getUsername(),
                        "server", serverName,
                        "prefix", lang.get("prefix")));
            } else {
                sendFallback(player, serverName);
                setSpawnServer(event, player);
            }
        } else {
            debug("log-no-record", Map.of(
                    "player", player.getUsername(),
                    "server", config.getSpawnServer(),
                    "prefix", lang.get("prefix")));
            setSpawnServer(event, player);
        }

        event.getInitialServer().ifPresent(server -> debug("log-initial-choice", Map.of(
                "player", player.getUsername(),
                "server", server.getServerInfo().getName(),
                "prefix", lang.get("prefix"))));
    }

    private void setSpawnServer(PlayerChooseInitialServerEvent event, Player player) {
        String spawn = config.getSpawnServer();
        Optional<RegisteredServer> spawnServer = proxy.getServer(spawn);
        if (spawnServer.isPresent()) {
            event.setInitialServer(spawnServer.get());
        } else {
            logger.warn("Spawn server '{}' is not registered in Velocity.", spawn);
        }
    }

    private void sendFallback(Player player, String missingServer) {
        String message = MessageFormatter.format(lang.get("server-missing"), Map.of(
                "prefix", lang.get("prefix"),
                "server", missingServer));
        Component component = serializer.deserialize(message);
        player.sendMessage(component);
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        if (!config.getRecording().isOnServerConnect()) {
            return;
        }
        event.getPlayer().getCurrentServer().ifPresent(server -> {
            String serverName = server.getServerInfo().getName();
            dataStore.setLastServer(event.getPlayer().getUniqueId(), serverName);
            debug("log-saved-server", Map.of(
                    "player", event.getPlayer().getUsername(),
                    "server", serverName,
                    "prefix", lang.get("prefix")));
        });
    }

    private void debug(String key, Map<String, String> placeholders) {
        if (!config.isDebug()) {
            return;
        }
        String message = MessageFormatter.format(lang.get(key), placeholders);
        logger.info(message);
    }
}
