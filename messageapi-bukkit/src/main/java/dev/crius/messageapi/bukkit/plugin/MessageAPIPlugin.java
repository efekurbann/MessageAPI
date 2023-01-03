package dev.crius.messageapi.bukkit.plugin;

import dev.crius.messageapi.MessageAPI;
import dev.crius.messageapi.bukkit.logger.BukkitLogger;
import dev.crius.messageapi.bukkit.scheduler.BukkitScheduler;
import dev.crius.messageapi.credentials.JedisCredentials;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageAPIPlugin extends JavaPlugin {

    @Getter private MessageAPI messageAPI;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        boolean debug = getConfig().getBoolean("credentials.debug");
        String host = getConfig().getString("credentials.host");
        String password = getConfig().getString("credentials.password");
        int port = getConfig().getInt("credentials.port");

        JedisCredentials credentials = new JedisCredentials(host, password, port);

        messageAPI = new MessageAPI(debug, credentials, new BukkitScheduler(this), new BukkitLogger(this));

        this.getLogger().info("Everything seems good. Plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (messageAPI != null)
            messageAPI.close();
    }
}
