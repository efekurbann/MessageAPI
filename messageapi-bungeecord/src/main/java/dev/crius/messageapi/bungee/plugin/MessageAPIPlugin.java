package dev.crius.messageapi.bungee.plugin;

import dev.crius.messageapi.MessageAPI;
import dev.crius.messageapi.bungee.logger.BungeeLogger;
import dev.crius.messageapi.bungee.plugin.config.Config;
import dev.crius.messageapi.bungee.scheduler.BungeeScheduler;
import dev.crius.messageapi.credentials.JedisCredentials;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class MessageAPIPlugin extends Plugin {

    private final Config configFile = new Config(this, "config.yml");

    @Getter private MessageAPI messageAPI;

    @Override
    public void onEnable() {
        this.configFile.create();
        Configuration config = configFile.getConfig();

        boolean debug = config.getBoolean("credentials.debug");
        String host = config.getString("credentials.host");
        String password = config.getString("credentials.password");
        int port = config.getInt("credentials.port");

        JedisCredentials credentials = new JedisCredentials(host, password, port);

        messageAPI = new MessageAPI(debug, credentials, new BungeeScheduler(this), new BungeeLogger(this));

        this.getLogger().info("Everything seems good. Plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (this.messageAPI != null)
            messageAPI.close();
    }

}
