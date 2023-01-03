package dev.crius.messageapi.listener;

import dev.crius.messageapi.MessageAPI;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.BinaryJedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class PubSubListener extends BinaryJedisPubSub {

    private final MessageAPI api;
    private final ReentrantLock lock = new ReentrantLock();
    private final Set<String> subscribed = ConcurrentHashMap.newKeySet();

    @Override
    public void subscribe(byte[]... channels) {
        this.lock.lock();
        try {
            for (byte[] channel : channels) {
                String channelName = new String(channel, StandardCharsets.UTF_8);
                if (this.subscribed.add(channelName)) {
                    super.subscribe(channel);
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void unsubscribe(byte[]... channels) {
        this.lock.lock();
        try {
            super.unsubscribe(channels);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        if (api.isDebug())
            api.getLogger().log("Successfully subscribed to channel: " +
                    new String(channel, StandardCharsets.UTF_8));
    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        String channelName = new String(channel, StandardCharsets.UTF_8);
        if (api.isDebug())
            api.getLogger().log("Successfully unsubscribed from channel: "
                    + channelName);
        this.subscribed.remove(channelName);
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        String channelName = new String(channel, StandardCharsets.UTF_8);
        try {
            api.handleMessage(channelName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
