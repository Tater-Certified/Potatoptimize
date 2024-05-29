package com.github.tatercertified.potatoptimize.mixin.startup.dfu;

import com.google.common.util.concurrent.MoreExecutors;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.datafixer.Schemas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * This code is from astei, LazyDFU developer: https://gist.github.com/astei/5f7fbf6efcf0fa55c14f7d36d48e8ccf
 */
@IfMinecraftVersion(minVersion = "1.19.3")
@Mixin(Schemas.class)
public class DFUMixin {
    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/Executors;newSingleThreadExecutor(Ljava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ExecutorService;"))
    private static ExecutorService createFixerUpper$directExecutor(ThreadFactory threadFactory) {
        return MoreExecutors.newDirectExecutorService();
    }
}
