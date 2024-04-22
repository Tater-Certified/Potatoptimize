package com.github.tatercertified.potatoptimize.mixin.threading.explosions;

import com.github.tatercertified.potatoptimize.utils.async.AsyncExplosion;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ExplosionThreadStopMixin {
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void shutdownExplosions(CallbackInfo ci) {
        AsyncExplosion.LOGGER.info("Attempting to stop Explosion Thread");
        AsyncExplosion.stopExecutor();
    }

    @Inject(method = "runServer", at = @At("HEAD"))
    private void startExplosionThread(CallbackInfo ci) {
        AsyncExplosion.LOGGER.info("Attempting to start Explosion Thread");
        AsyncExplosion.initExecutor(true, 1);
    }
}
