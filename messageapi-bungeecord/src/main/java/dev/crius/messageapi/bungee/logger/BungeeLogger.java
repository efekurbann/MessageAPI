package dev.crius.messageapi.bungee.logger;

import dev.crius.messageapi.logger.Logger;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;

@RequiredArgsConstructor
public class BungeeLogger implements Logger {

    private final Plugin plugin;

    @Override
    public void log(String message) {
        this.plugin.getLogger().info(message);
    }

}
