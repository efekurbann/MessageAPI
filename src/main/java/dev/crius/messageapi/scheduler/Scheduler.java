package dev.crius.messageapi.scheduler;

public interface Scheduler {

    void runAsync(Runnable runnable);

    void runSync(Runnable runnable);

    void runSyncTimer(Runnable runnable, long delay, long period);

    void runAsyncTimer(Runnable runnable, long delay, long period);

}
