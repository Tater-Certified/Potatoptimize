package com.github.tatercertified.potatoptimize.mixin.logic.main_thread;

import com.github.tatercertified.potatoptimize.utils.ServerMinionThread;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.Util;
import net.minecraft.util.thread.NameableExecutor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.TimeUnit;

@IfModAbsent(value = "stutterfix")
@Mixin(Util.class)
public class UtilClassMixin {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;createWorker(Ljava/lang/String;)Lnet/minecraft/util/thread/NameableExecutor;"))
    private static NameableExecutor redirectCreateWorker(String name) {
        // TODO Do smart calculations to find the optimal amount of cores to use
        int i = Math.min(8, Math.max(Runtime.getRuntime().availableProcessors() - 2, 1));
        return new NameableExecutor(new java.util.concurrent.ThreadPoolExecutor(i, i,0L, TimeUnit.MILLISECONDS, new java.util.concurrent.LinkedBlockingQueue<>(), target -> new ServerMinionThread(target, "Main", -1)) {

            @Override
            protected void terminated() {
                ExceptionHandlerInvoker.getLogger().debug("{} shutdown", "Main");
                super.terminated();
            }

        });
    }
}
