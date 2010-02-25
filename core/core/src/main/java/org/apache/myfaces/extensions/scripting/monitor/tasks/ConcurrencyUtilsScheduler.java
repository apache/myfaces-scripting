package org.apache.myfaces.extensions.scripting.monitor.tasks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ConcurrencyUtilsScheduler implements Scheduler {

    private ScheduledExecutorService scheduler;

    private long timeout;

    // ------------------------------------------ Constructors

    public ConcurrencyUtilsScheduler(long timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException(
                    "The given timeout must not be negative.");
        }

        this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
        this.timeout = timeout;
    }

    // ------------------------------------------ Public methods

    public void schedule(Runnable runnable) {
        scheduler.scheduleAtFixedRate(
                runnable, timeout, timeout, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }

}
