package dev.crius.messageapi.channel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.crius.messageapi.MessageAPI;
import dev.crius.messageapi.channel.listener.ChannelListener;
import dev.crius.messageapi.codec.GsonCodec;
import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

@Getter
public class Channel<T> {

    private final MessageAPI api;
    private final String name;
    private final GsonCodec<T> codec;
    private final Class<T> clazz;
    private ChannelListener<T> listener;

    public Channel(MessageAPI api, String name, Class<T> clazz) {
        this.api = api;
        this.name = name;
        this.codec = new GsonCodec<>(new Gson(), TypeToken.get(clazz));
        this.clazz = clazz;

        api.addChannel(this);
    }

    public void sendMessage(T object) {
        api.getScheduler().runAsync(() -> {
            try (Jedis jedis = api.getPool().getResource()) {
                jedis.publish(name.getBytes(StandardCharsets.UTF_8), codec.encode(object));
            }
        });
    }

    /**
     * Not recommended but may be useful in some cases where you can't use async.
     * @param object object
     */
    public void sendMessageSync(T object) {
        try (Jedis jedis = api.getPool().getResource()) {
            jedis.publish(name.getBytes(StandardCharsets.UTF_8), codec.encode(object));
        }
    }

    public void listen(ChannelListener<T> listener) {
        this.listener = listener;
    }

    public void onMessage(T message) {
        if (this.listener == null) return;

        api.getScheduler().runAsync(() -> listener.onMessage(message));
    }

    public void onMessage(byte[] message) {
        onMessage(this.codec.decode(message));
    }

}
