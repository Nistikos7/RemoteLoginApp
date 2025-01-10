package com.mycompany.util;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class ThreadMonitor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ThreadMonitor.class.getName());
    private final ThreadPoolExecutor executor;
    private final int delaySeconds;
    private boolean isRunning = true;

    public ThreadMonitor(ThreadPoolExecutor executor, int delaySeconds) {
        this.executor = executor;
        this.delaySeconds = delaySeconds;
    }

    @Override
    public void run() {
        while (isRunning) {
            LOGGER.info(String.format(
                "Thread Pool Status: [Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s]",
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount(),
                executor.isShutdown(),
                executor.isTerminated()));
            try {
                Thread.sleep(delaySeconds * 1000);
            } catch (InterruptedException e) {
                LOGGER.warning("ThreadMonitor interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        isRunning = false;
    }
}