package com.github.tatercertified.potatoptimize.utils;

import com.github.tatercertified.potatoptimize.mixin.logic.main_thread.ExceptionHandlerInvoker;
import com.google.common.base.Preconditions;

public class ServerWorkerThreadWrapper implements Runnable {
    private final Runnable internalRunnable;

    public ServerWorkerThreadWrapper(Runnable runnable) {
        this.internalRunnable = Preconditions.checkNotNull(runnable, "internalRunnable");
    }

    @Override
    public final void run() {
        try {
            this.internalRunnable.run();
        }
        catch (Throwable throwable) {
            ExceptionHandlerInvoker.invokeUncaughtExceptionHandler(Thread.currentThread(), throwable);
        }
    }
}
