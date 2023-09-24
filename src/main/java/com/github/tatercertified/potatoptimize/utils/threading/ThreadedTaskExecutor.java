package com.github.tatercertified.potatoptimize.utils.threading;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.thread.ReentrantThreadExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ThreadedTaskExecutor {
    private final ReentrantThreadExecutor<?> parent;
    private final int maxThreads;
    private final ExecutorService executor;

    public ThreadedTaskExecutor(ReentrantThreadExecutor<?> parent, int maxThreads) {
        this.parent = parent;
        this.maxThreads = maxThreads;
        this.executor = Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * Runs a task with the max amount of threads possible
     *
     * @param task Runnable task
     */
    public void executeThreadedTask(Runnable task) {
        this.executor.execute(task);
    }

    /**
     * Runs a CompletableFuture
     * @param future CompletableFuture
     */
    @Deprecated(forRemoval = true) // This isn't how futures work
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
     *
     * @param threshold Objects per thread
     * @param map       ConcurrentHashMap for "forEach"
     */
    public <V> void concurrentForEach(int threshold, ConcurrentHashMap<?, V> map, Consumer<V> action) {
        final var spliterator = map.values().spliterator();

        if (spliterator.estimateSize() < threshold) {
            spliterator.forEachRemaining(action);
            return;
        }

        final var split = split(spliterator, maxThreads);
        final var atomic = new AtomicInteger(split.size());
        final var queue = new ArrayBlockingQueue<Throwable>(maxThreads);

        for (final var spliter : split) {
            executeThreadedTask(() -> {
                try {
                    spliter.forEachRemaining(action);
                } catch (Throwable t) {
                    queue.add(t);
                } finally {
                    atomic.decrementAndGet();
                }
            });
        }

        parent.runTasks(() -> atomic.getAcquire() == 0);

        if (!queue.isEmpty()) {
            final var report = CrashReport.create(queue.poll(), "Failure ticking entities concurrently:");

            Throwable t;
            while ((t = queue.poll()) != null) {
                report.addElement("Suppressed:").add(t.getLocalizedMessage(), t);
            }

            throw new CrashException(report);
        }
    }

    private static <V> Collection<Spliterator<V>> split(Spliterator<V> spliterator, int amount) {
        final var list = new ArrayList<Spliterator<V>>(amount);
        list.add(spliterator);


        for (int i = 0, a = 1, c = 1; c < amount; i++, c++) {
            if (i == a) {
                a += a;
                i = 0;
            }
            // System.err.printf("split(%s, %d) -> i%d, a%d, c%d\n", spliterator, amount, i, a, c);
            final var sub = list.get(i).trySplit();
            if (sub == null) break;
            list.add(sub);
        }

        return list;
    }
}
