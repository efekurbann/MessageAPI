package dev.crius.messageapi.velocity.plugin;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.crius.messageapi.MessageAPI;
import dev.crius.messageapi.credentials.JedisCredentials;
import dev.crius.messageapi.velocity.logger.VelocityLogger;
import dev.crius.messageapi.velocity.plugin.config.Config;
import dev.crius.messageapi.velocity.scheduler.VelocityScheduler;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(
        id = "messageapi",
        name = "MessageAPIPlugin",
        version = "2.0.0",
        url = "https://github.com/CriusDevelopment/MessageAPI",
        authors = {"CriusDevelopment", "hyperion"}
)
@Getter
public class MessageAPIPlugin {

    private final ProxyServer server;
    private final org.slf4j.Logger logger;
    private final Path dataDirectory;

    private Config config;
    private MessageAPI messageAPI;

    @Inject
    public MessageAPIPlugin(ProxyServer server, org.slf4j.Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new Config(this, Paths.get(dataDirectory.toString(), "config.yml"));
        config.create();

        boolean debug = config.getRoot().getNode("credentials", "debug").getBoolean();
        String host = config.getRoot().getNode("credentials", "host").getString();
        String password = config.getRoot().getNode("credentials", "password").getString();
        int port = config.getRoot().getNode("credentials", "port").getInt();

        JedisCredentials credentials = new JedisCredentials(host, password, port);

        messageAPI = new MessageAPI(debug, credentials, new VelocityScheduler(server, this), new VelocityLogger(logger));

        this.getLogger().info("Everything seems good. Plugin enabled!");

        server.getEventManager().register(this, this);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (messageAPI != null)
            messageAPI.close();
    }


}
