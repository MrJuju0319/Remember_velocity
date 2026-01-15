package com.remembervelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "remember_velocity",
        name = "RememberVelocity",
        version = "1.0.0",
        description = "Send players to spawn or their last known server on Velocity."
)
public class RememberVelocityPlugin {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    private Config config;
    private Lang lang;
    private DataStore dataStore;

    @Inject
    public RememberVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            ConfigLoader loader = new ConfigLoader(dataDirectory);
            config = loader.loadConfig();
            lang = loader.loadLang(config.getLangFile());
            dataStore = DataStoreFactory.create(config, dataDirectory);
            proxy.getEventManager().register(this, new PlayerListener(proxy, logger, config, lang, dataStore));
            logger.info("RememberVelocity chargé. Stockage: {}", config.getStorage().getType());
        } catch (IOException e) {
            logger.error("Impossible de charger la configuration.", e);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (dataStore != null) {
            dataStore.close();
        }
    }
}
