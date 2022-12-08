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

        JedisCredentials credentials = new JedisCredentials(Objects.requireNonNull(this.getConfig().getConfigurationSection("redis")));

        // you can disable messages such as "subscribed" etc. by disabling debug mode.
        this.api = new MessageAPI(this, true, credentials);

        // it is highly suggested to save this object for later, creating a new channel object may lead to problems
        Channel<TestObject> objectChannel = new Channel<>(api, "ObjectListener", TestObject.class);

        // set a callback to use when there is a new message
        // this will be called asynchronously.
        objectChannel.listen(message -> getLogger().info(message.message));

        // we must call this method after setting everything up otherwise your listeners will not function
        this.api.addChannel(objectChannel);

        // since we subscribe to channels asynchronously, we will wait for a while to send a message.
        Bukkit.getScheduler().runTaskLater(this, () -> objectChannel.sendMessage(new TestObject("test")), 5);
    }

    @Override
    public void onDisable() {
        // no need to call the close method since it will be closed automatically
    }
}
