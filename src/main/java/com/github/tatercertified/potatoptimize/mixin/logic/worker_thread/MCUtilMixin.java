package com.github.tatercertified.potatoptimize.mixin.logic.worker_thread;

import com.github.tatercertified.potatoptimize.utils.ServerWorkerThreadWrapper;
import com.github.tatercertified.potatoptimize.utils.threading.ThreadUtils;
import com.google.common.util.concurrent.MoreExecutors;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

// Credit to TitaniumTown and Jettpack
@IfModAbsent(value = "modernfix")
@Mixin(Util.class)
public abstract class MCUtilMixin {
    @Shadow
    private static int getMaxBackgroundThreads() {
        return 0;
    }

    /**
     * @author QPCrummer
     * @reason Optimize worker threads
     */
    @Overwrite
    private static ExecutorService createWorker(String name) {
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxBackgroundThreads());
        ExecutorService executorService;
        if (i <= 0) {
            executorService = MoreExecutors.newDirectExecutorService();
        } else {
            executorService = new AbstractExecutorService() {
                private volatile boolean shutdown;

                @Override
                public List<Runnable> shutdownNow() {
                    this.shutdown = true;
                    return Collections.emptyList();
                }

                @Override
                public void shutdown() {
                    this.shutdown = true;
                }

                @Override
                public boolean isShutdown() {
                    return this.shutdown;
                }

                @Override
                public boolean isTerminated() {
                    return this.shutdown;
                }

                @Override
                public boolean awaitTermination(long l2, @NotNull TimeUnit timeUnit) {
                    if (!this.shutdown) {
                        throw new UnsupportedOperationException();
                    }
                    return true;
                }
                @Override
                public void execute(@NotNull Runnable runnable) {
                    ThreadUtils.asyncExecutor.execute(new ServerWorkerThreadWrapper(runnable));
                }
            };
        }

        return executorService;
    }
}
