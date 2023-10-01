package com.axreng.backend.infrastructure.threads;

public interface TaskQueueInterface {
    void addTask(Runnable task);
    void start();
    void stop();
}
