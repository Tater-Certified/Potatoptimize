package com.github.tatercertified.potatoptimize.mixin.logic.main_thread;

import com.github.tatercertified.potatoptimize.utils.ServerMinionThread;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(Util.class)
public class UtilClassMixin {
    // TODO Determine the hit to performance of this by making it not final
    @Shadow @Final @Mutable
    private final static ExecutorService MAIN_WORKER_EXECUTOR = createWorker();

    @Unique
    private static ExecutorService createWorker() {
        // TODO Do smart calculations to find the optimal amount of cores to use
        int i = Math.min(8, Math.max(Runtime.getRuntime().availableProcessors() - 2, 1));
        // PaperMC method below
        //i = Integer.getInteger("Paper.WorkerThreadCount", i);
        return new java.util.concurrent.ThreadPoolExecutor(i, i,0L, TimeUnit.MILLISECONDS, new java.util.concurrent.LinkedBlockingQueue<>(), target -> new ServerMinionThread(target, "Main", -1)) {

            @Override
            protected void terminated() {
                ExceptionHandlerInvoker.getLogger().debug("{} shutdown", "Main");
                super.terminated();
            }

        };
    }
}
