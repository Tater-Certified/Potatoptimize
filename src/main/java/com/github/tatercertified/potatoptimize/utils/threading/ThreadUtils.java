package com.github.tatercertified.potatoptimize.utils.threading;

import com.github.tatercertified.potatoptimize.mixin.logic.main_thread.ExceptionHandlerInvoker;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.util.logging.UncaughtExceptionHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    public static final ThreadPoolExecutor asyncExecutor = new ThreadPoolExecutor(
                    0, 2, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadFactoryBuilder()
                            .setNameFormat("Paper Async Task Handler Thread - %1$d")
                            .setUncaughtExceptionHandler(new UncaughtExceptionHandler(ExceptionHandlerInvoker.getLogger()))
                            .build()
    );
}
