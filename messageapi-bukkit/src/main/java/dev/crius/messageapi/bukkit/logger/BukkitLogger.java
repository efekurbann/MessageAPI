package dev.crius.messageapi.bukkit.logger;

import dev.crius.messageapi.logger.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class BukkitLogger implements Logger {

    private final JavaPlugin plugin;

    @Override
    public void log(String message) {
        plugin.getLogger().info(message);
    }
}
