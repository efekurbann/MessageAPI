package dev.crius.messageapi.sponge.plugin;

import com.google.inject.Inject;
import dev.crius.messageapi.MessageAPI;
import dev.crius.messageapi.credentials.JedisCredentials;
import dev.crius.messageapi.sponge.logger.SpongeLogger;
import dev.crius.messageapi.sponge.scheduler.SpongeScheduler;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("messageapi")
@Getter
public class MessageAPIPlugin {


    private final PluginContainer container;
    private final Logger logger;
    private final ConfigManager configManager;
    private final Game game;

    private MessageAPI messageAPI;

    @Inject
    MessageAPIPlugin(final PluginContainer container, final Logger logger, final Game game, final ConfigManager configManager) {
        this.container = container;
        this.logger = logger;
        this.configManager = configManager;
        this.game = game;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        CommentedConfigurationNode root;
        try {
            ConfigurationLoader<CommentedConfigurationNode> loader = configManager.sharedConfig(container).config();
            root = loader.load();
            if (root.node("credentials", "debug").virtual())
                root.node("credentials", "debug").set(true);
            if (root.node("credentials", "host").virtual())
                root.node("credentials", "host").set("host");
            if (root.node("credentials", "password").virtual())
                root.node("credentials", "password").set("password");
            if (root.node("credentials", "port").virtual())
                root.node("credentials", "port").set(1234);
            loader.save(root);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        boolean debug = root.node("credentials", "debug").getBoolean();
        String host = root.node("credentials", "host").getString();
        String password = root.node("credentials", "password").getString();
        int port = root.node("credentials", "port").getInt();

        JedisCredentials credentials = new JedisCredentials(host, password, port);

        messageAPI = new MessageAPI(debug, credentials, new SpongeScheduler(container), new SpongeLogger(logger));

        this.getLogger().info("Everything seems good. Plugin enabled!");

        game.eventManager().registerListeners(container, this);
    }

    @Listener
    public void onServerStopping(final StoppingEngineEvent<Server> event) {
        if (messageAPI != null)
            messageAPI.close();
    }

}
