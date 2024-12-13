package com.searchterm.backend.infrastructure.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolService implements TaskQueue {
    private static final ThreadPoolService instance = new ThreadPoolService();

    private static final Integer THREAD_TIMEOUT = 1200;
    private static final Integer THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolService.class);

    private ThreadPoolService() {
    }

    public static ThreadPoolService getInstance() {
        logger.info("Thread pool started with " + THREAD_POOL_SIZE + " threads");
        return instance;
    }


    private static Executor getExecutorInstance () {
        return executor;
    }

    @Override
    public  Executor getExecutor() {
        return getExecutorInstance();
    }

    @Override
    public void addTask(Runnable task) {
        try {
            executor.submit(task);
        } catch (Exception e) {
            logger.error("Error adding task to thread pool: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {

            if (!executor.awaitTermination(THREAD_TIMEOUT, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                if (!executor.awaitTermination(100, TimeUnit.SECONDS))
                    logger.warn("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            logger.error("Error stopping thread pool: " + ex.getMessage());
        }
    }
}
