package com.axreng.backend.infrastructure.threads;

import java.util.concurrent.Executor;

public interface TaskQueue {
    void addTask(Runnable task);
    void stop();

    Executor getExecutor();
}
