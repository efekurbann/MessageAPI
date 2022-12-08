package dev.crius.messageapi;

import dev.crius.messageapi.channel.Channel;
import dev.crius.messageapi.credentials.JedisCredentials;
import dev.crius.messageapi.listener.PubSubListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MessageAPI {

    private final Map<String, Channel<?>> channelMap = new ConcurrentHashMap<>();
    @Getter private final JavaPlugin plugin;
    @Getter private final Logger logger;
    @Getter private final boolean debug;
    @Getter private final JedisPool pool;
    private final PubSubListener listener;

    public MessageAPI(JavaPlugin plugin, boolean debug, JedisCredentials credentials) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.debug = debug;
        this.listener = new PubSubListener(this);

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);

        if (!credentials.getPassword().trim().isEmpty()) {
            this.pool = new JedisPool(config, credentials.getIp(),
                    credentials.getPort(), 2000, credentials.getPassword());
        } else {
            this.pool = new JedisPool(config, credentials.getIp(),
                    credentials.getPort(), 2000);
        }

        try (Jedis jedis = this.pool.getResource()) {
            jedis.ping();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!listener.isSubscribed()) {
                    this.cancel();
                    return;
                }

                if (pool.isClosed()) {
                    this.cancel();
                    return;
                }

                for (String channel : channelMap.keySet()) {
                    try (Jedis jedis = pool.getResource()) {
                        jedis.subscribe(listener, channel.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 2, 2);

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onDisable(PluginDisableEvent event) {
                if (plugin == event.getPlugin()) close();
            }
        }, plugin);
    }

    public void close() {
        if (this.listener != null) {
            this.listener.unsubscribe();
        }

        if (this.pool != null) {
            this.pool.close();
        }
    }

    public void handleMessage(String channelName, byte[] message) {
        if (!channelMap.containsKey(channelName)) return;

        Channel<?> channel = channelMap.get(channelName);
        channel.onMessage(message);
    }

    public void addChannel(Channel<?> channel) {
        this.channelMap.put(channel.getName(), channel);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = this.pool.getResource()) {
                jedis.subscribe(listener, channel.getName().getBytes(StandardCharsets.UTF_8));
            }
        });
    }

}
