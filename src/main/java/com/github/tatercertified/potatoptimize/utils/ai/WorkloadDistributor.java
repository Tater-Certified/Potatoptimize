package com.github.tatercertified.potatoptimize.utils.ai;

import java.util.ArrayDeque;
import java.util.Objects;

public class WorkloadDistributor {
    private final long taskTime;
    private final long maxTasks;

    private final long tickDelay;

    private long desiredStart = System.nanoTime();
    public WorkloadDistributor(long taskTime, long maxTasks, long tickDelay) {
        this.taskTime = taskTime;
        this.maxTasks = maxTasks;
        this.tickDelay = tickDelay;
    }
    ArrayDeque<Runnable> tasks = new ArrayDeque<>();

    public void addTask(Runnable task) {
        tasks.offer(task);
        if(tasks.size() > maxTasks) tasks.poll();
    }

    public void tick() {
        if(tasks.isEmpty()) return;
        if(System.nanoTime() < desiredStart) return;
        long start = System.nanoTime();
        while(!tasks.isEmpty() && System.nanoTime() - start < taskTime) {
            Objects.requireNonNull(tasks.poll()).run();
        }
        desiredStart = System.nanoTime() + tickDelay;
    }
}
