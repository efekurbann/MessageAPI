package dev.crius.messageapi.velocity.scheduler;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.crius.messageapi.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VelocityScheduler implements Scheduler {

    private final ProxyServer server;
    private final Object plugin;

    @Override
    public void runAsync(Runnable runnable) {
        server.getScheduler().buildTask(plugin, runnable).schedule();
    }

    @Override
    public void runSync(Runnable runnable) {
        server.getScheduler().buildTask(plugin, runnable).schedule();
    }

    @Override
    public void runSyncTimer(Runnable runnable, long delay, long period) {
        server.getScheduler().buildTask(plugin, runnable)
                .delay(delay * 50, TimeUnit.MILLISECONDS)
                .repeat(period * 50, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public void runAsyncTimer(Runnable runnable, long delay, long period) {
        server.getScheduler().buildTask(plugin, runnable)
                .delay(delay * 50, TimeUnit.MILLISECONDS)
                .repeat(period * 50, TimeUnit.MILLISECONDS).schedule();
    }
}
