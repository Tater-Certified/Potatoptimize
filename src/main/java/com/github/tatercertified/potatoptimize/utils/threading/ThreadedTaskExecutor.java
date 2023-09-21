package com.github.tatercertified.potatoptimize.utils.threading;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class ThreadedTaskExecutor {
    private final int maxThreads;
    private final ExecutorService executor;

    public ThreadedTaskExecutor(int maxThreads) {
        this.maxThreads = maxThreads;
        this.executor = Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * Runs a task with the max amount of threads possible
     * @param task Runnable task
     */
    public void executeThreadedTask(Runnable task) {
        this.executor.execute(task);
    }

    /**
     * Runs a CompletableFuture
     * @param future CompletableFuture
     */
    public void executeThreadedFuture(CompletableFuture<?> future) {
        this.executor.execute( () -> {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Runs "forEach" concurrently
     * @param threshold Objects per thread
     * @param map ConcurrentHashMap for "forEach"
     */
    public void concurrentForEach(int threshold, ConcurrentHashMap<?, ?> map, Consumer<? super Object> action) {
        map.forEach(threshold, (key, value) -> action.accept(value));
    }
}
