package com.github.tatercertified.potatoptimize.mixin.threading.explosions;

import com.github.tatercertified.potatoptimize.utils.async.AsyncExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Leaf patch #0039
 */
@Mixin(ExplosionImpl.class)
public abstract class AsyncExplosionMixin {
    private static CompletableFuture<Float> getExposureAsync(Vec3d source, Entity entity) {
        return CompletableFuture.supplyAsync(() -> ExplosionImpl.calculateReceivedDamage(source, entity), AsyncExplosion.EXECUTOR);
    }

    @Redirect(method = "damageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionImpl;calculateReceivedDamage(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)F"))
    private float makeAsync(Vec3d source, Entity entity) {
        try {
            return getExposureAsync(source, entity).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
