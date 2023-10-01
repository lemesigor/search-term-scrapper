package com.axreng.backend.infrastructure.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ThreadPoolService  {
    private static final ThreadPoolService instance = new ThreadPoolService();

    private static final Integer THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
   private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final Integer THREAD_TIMEOUT = 1200;

    private ThreadPoolService() {}

    public static ThreadPoolService getInstance() {
        System.out.println("THREAD_POOL_SIZE: " + THREAD_POOL_SIZE);
        return instance;
    }


    public ExecutorService getExecutor() {
        return executor;
    }


//    public void addTask(Function<Void, Void> task) {
//        try {
//            executor.submit(this::task);
//
//            if (!executor.awaitTermination(THREAD_TIMEOUT, TimeUnit.SECONDS) && count.get() > 0) {
//                executor.shutdownNow(); // Cancel currently executing tasks
//                if (!executor.awaitTermination(100, TimeUnit.SECONDS))
//                    logger.warn("Pool did not terminate");
//            }
//        } catch (InterruptedException ex) {
//            executor.shutdownNow();
//            Thread.currentThread().interrupt();
//        }
//    }

    }
