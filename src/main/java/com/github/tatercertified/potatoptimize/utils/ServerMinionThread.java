package com.github.tatercertified.potatoptimize.utils;

import com.github.tatercertified.potatoptimize.mixin.logic.main_thread.ExceptionHandlerInvoker;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerMinionThread extends Thread {
    private static final AtomicInteger threadId = new AtomicInteger(1);

    public ServerMinionThread(Runnable target, String poolName, int prioritityModifier) {
        super(target, "PotatoptimizeWorkerThread-" + poolName + "-" + threadId.getAndIncrement());
        setPriority(Thread.NORM_PRIORITY+prioritityModifier);
        this.setDaemon(true);
        this.setUncaughtExceptionHandler(ExceptionHandlerInvoker::invokeUncaughtExceptionHandler);
    }
}
