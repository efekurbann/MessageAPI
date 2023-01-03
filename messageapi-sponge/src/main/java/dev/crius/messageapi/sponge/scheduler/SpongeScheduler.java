package dev.crius.messageapi.sponge.scheduler;

import dev.crius.messageapi.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SpongeScheduler implements Scheduler {

    private final PluginContainer pluginContainer;

    @Override
    public void runAsync(Runnable runnable) {
        Sponge.asyncScheduler().executor(pluginContainer).submit(runnable);
    }

    @Override
    public void runSync(Runnable runnable) {
        Sponge.server().scheduler().executor(pluginContainer).submit(runnable);
    }

    @Override
    public void runSyncTimer(Runnable runnable, long delay, long period) {
        Sponge.server().scheduler().executor(pluginContainer).scheduleAtFixedRate(runnable, delay * 50, period * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAsyncTimer(Runnable runnable, long delay, long period) {
        Sponge.asyncScheduler().executor(pluginContainer).scheduleAtFixedRate(runnable, delay * 50, period * 50, TimeUnit.MILLISECONDS);
    }
}
