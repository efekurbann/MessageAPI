package dev.crius.messageapi.plugin;

import dev.crius.messageapi.MessageAPI;
import dev.crius.messageapi.channel.Channel;
import dev.crius.messageapi.credentials.JedisCredentials;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MessageAPIPlugin extends JavaPlugin {

    @Getter private MessageAPI api;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        /*JedisCredentials credentials = new JedisCredentials(
                "host",
                "password",
                18273
        );*/

        JedisCredentials credentials = new JedisCredentials(
                this.getConfig().getConfigurationSection("redis")
        );

        this.api = new MessageAPI(this, true, credentials);

        Channel<TestObject> objectChannel = new Channel<>(api,
                "ObjectListener", TestObject.class);

        objectChannel.listen(message -> getLogger().info(message.message));

        this.api.addChannel(objectChannel);

        Bukkit.getScheduler().runTaskLater(this, () ->
                objectChannel.sendMessage(new TestObject("test")), 5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
