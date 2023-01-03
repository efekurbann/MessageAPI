package dev.crius.messageapi;

import dev.crius.messageapi.channel.Channel;
import dev.crius.messageapi.credentials.JedisCredentials;
import dev.crius.messageapi.listener.PubSubListener;
import dev.crius.messageapi.logger.Logger;
import dev.crius.messageapi.scheduler.Scheduler;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageAPI {

    @Getter private static MessageAPI instance;

    private final Map<String, Channel<?>> channelMap = new ConcurrentHashMap<>();
    @Getter private final Logger logger;
    @Getter private final boolean debug;
    @Getter private final JedisPool pool;
    private final PubSubListener listener;

    @Getter private final Scheduler scheduler;

    public MessageAPI(boolean debug, JedisCredentials credentials, Scheduler scheduler, Logger logger) {
        this.debug = debug;
        this.listener = new PubSubListener(this);
        this.scheduler = scheduler;
        this.logger = logger;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);

        if (!credentials.getPassword().trim().isEmpty()) {
            this.pool = new JedisPool(config, credentials.getIp(), credentials.getPort(), 2000, credentials.getPassword());
        } else {
            this.pool = new JedisPool(config, credentials.getIp(), credentials.getPort(), 2000);
        }

        scheduler.runAsync(() -> {
            try (Jedis jedis = this.pool.getResource()) {
                jedis.subscribe(this.listener, "messageapi".getBytes(StandardCharsets.UTF_8));
            }
        });

        scheduler.runAsyncTimer(() -> {
            if (!listener.isSubscribed()) return;

            for (String channel : channelMap.keySet()) {
                listener.subscribe(channel.getBytes(StandardCharsets.UTF_8));
            }
        }, 2, 2);

        instance = this;
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
    }

    public <T> Channel<T> getChannel(String name, Class<T> clazz) {
        Channel<?> channel = this.channelMap.get(name);
        if (channel.getClazz() != clazz) return null;

        return (Channel<T>) channel;
    }

}
