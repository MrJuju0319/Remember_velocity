package com.example.remembervelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.util.DataDirectory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Plugin(
        id = "remembervelocity",
        name = "RememberVelocity",
        version = "1.0.0",
        description = "Send players to spawn or their last server",
        authors = {"OpenAI"}
)
public class RememberVelocity {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private Config config;
    private Storage storage;
    private Language language;

    @Inject
    public RememberVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, this);
        loadConfig();
        loadLanguage();
        initStorage();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (storage != null) {
            storage.close();
        }
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        if (config.isUseLastServer()) {
            Optional<String> lastServer = storage.getLastServer(player.getUniqueId());
            if (lastServer.isPresent()) {
                Optional<RegisteredServer> server = proxy.getServer(lastServer.get());
                if (server.isPresent()) {
                    event.setInitialServer(server.get());
                    return;
                }
                logger.warn(language.message("last-server-missing", Map.of(\"server\", lastServer.get())));
            }
        }
        String spawnName = config.getSpawnServer();
        Optional<RegisteredServer> spawn = proxy.getServer(spawnName);
        if (spawn.isPresent()) {
            event.setInitialServer(spawn.get());
        } else {
            logger.warn(language.message("spawn-missing", Map.of(\"server\", spawnName)));
        }
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        if (!config.isSaveOnPostConnect()) {
            return;
        }
        event.getPlayer().getCurrentServer().ifPresent(serverConnection -> {
            ServerInfo info = serverConnection.getServerInfo();
            storage.setLastServer(event.getPlayer().getUniqueId(), info.getName());
        });
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        if (!config.isSaveOnDisconnect()) {
            return;
        }
        event.getPlayer().getCurrentServer().ifPresent(serverConnection -> {
            storage.setLastServer(event.getPlayer().getUniqueId(), serverConnection.getServerInfo().getName());
        });
    }

    private void loadConfig() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            logger.error("Impossible de créer le dossier de données.", e);
        }

        Path configPath = dataDirectory.resolve("config.yml");
        copyDefaultResource("config.yml", configPath);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                Config loaded = yaml.loadAs(reader, Config.class);
                if (loaded != null) {
                    this.config = loaded;
                    return;
                }
            } catch (IOException e) {
                logger.warn("Impossible de lire config.yml, utilisation des valeurs par défaut.", e);
            }
        }

        this.config = new Config();
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            yaml.dump(config, writer);
        } catch (IOException e) {
            logger.warn("Impossible d'écrire config.yml.", e);
        }
    }

    private void loadLanguage() {
        Path languagePath = dataDirectory.resolve(config.getLanguageFile());
        copyDefaultResource("lang.yml", languagePath);
        this.language = Language.load(languagePath);
    }

    private void initStorage() {
        String type = config.getStorage().getType().toLowerCase();
        switch (type) {
            case "mariadb" -> storage = new MariaDbStorage(config.getStorage().getMariadb());
            case "yaml" -> storage = new YamlStorage(dataDirectory.resolve(config.getStorage().getYaml().getFile()));
            default -> {
                logger.warn(language.message("storage-unknown", Map.of(\"type\", type)));
                storage = new YamlStorage(dataDirectory.resolve(config.getStorage().getYaml().getFile()));
            }
        }
    }

    private void copyDefaultResource(String resourceName, Path destination) {
        if (Files.exists(destination)) {
            return;
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                return;
            }
            Files.createDirectories(destination.getParent());
            Files.copy(input, destination);
        } catch (IOException ignored) {
        }
    }
}
