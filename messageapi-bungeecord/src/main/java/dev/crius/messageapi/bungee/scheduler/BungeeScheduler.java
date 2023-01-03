package dev.crius.messageapi.bungee.scheduler;

import dev.crius.messageapi.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BungeeScheduler implements Scheduler {

    private final Plugin plugin;

    @Override
    public void runAsync(Runnable runnable) {
        plugin.getProxy().getScheduler().runAsync(plugin, runnable);
    }

    @Override
    public void runSync(Runnable runnable) {
        // there is no "single thread" design in bungee
        plugin.getProxy().getScheduler().runAsync(plugin, runnable);
    }

    @Override
    public void runSyncTimer(Runnable runnable, long delay, long period) {
        plugin.getProxy().getScheduler().schedule(plugin, runnable, delay * 50, period * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAsyncTimer(Runnable runnable, long delay, long period) {
        plugin.getProxy().getScheduler().schedule(plugin, runnable, delay * 50, period * 50, TimeUnit.MILLISECONDS);
    }
}
