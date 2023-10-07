package com.github.tatercertified.potatoptimize.mixin.logic.main_thread;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerThreadPriorityMixin {
    @Inject(method = "startServer", at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;availableProcessors()I"))
    private static <S> void setThreadPriority(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cir, @Local(ordinal = 0) Thread thread) {
        thread.setPriority(Thread.NORM_PRIORITY+2);
    }
}
