package com.axreng.backend.infrastructure.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolService implements TaskQueue {
    private static final ThreadPoolService instance = new ThreadPoolService();

    private static final Integer THREAD_TIMEOUT = 1200;
    private static final Integer THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private ThreadPoolService() {
    }

    public static ThreadPoolService getInstance() {
        //TODO : trocar por logger factory
        System.out.println("THREAD_POOL_SIZE: " + THREAD_POOL_SIZE);
        return instance;
    }


    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public void addTask(Runnable task) {
        try {
            executor.submit(task);
        } catch (Exception e) {
            System.out.println("Erro ao executar tarefa");
        }
    }

    @Override
    public void stop() {
        try {

            if (!executor.awaitTermination(THREAD_TIMEOUT, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                if (!executor.awaitTermination(100, TimeUnit.SECONDS))
                    System.out.println("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
