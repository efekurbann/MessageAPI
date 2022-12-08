package dev.crius.messageapi.channel.listener;

public interface ChannelListener<T> {

    void onMessage(T message);

}
