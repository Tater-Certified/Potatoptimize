package com.github.tatercertified.potatoptimize.mixin.world.explosion;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * Credit to PaperMC patch #0334 and patch #0039
 */
@IfModAbsent(value = "lithium")
@Mixin(ExplosionImpl.class)
public class OptimizedExplosionMixin {
    @Shadow @Final private ServerWorld world;

    public OptimizedExplosionMixin() {
    }

    @Redirect(method = "getBlocksToDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState redirectFluidState(ServerWorld instance, BlockPos blockPos, @Local(ordinal = 0) BlockState blockState) {
        return blockState.getFluidState();
    }

    @Redirect(method = "damageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
    private List<Entity> optimizeGetEntities(ServerWorld instance, Entity entity, Box box, @Local(ordinal = 0) int k, @Local(ordinal = 0) int r, @Local(ordinal = 0) int t, @Local(ordinal = 0) int l, @Local(ordinal = 0) int s, @Local(ordinal = 0) int u) {
        return this.world.getOtherEntities(entity, new Box(k, r, t, l, s, u), (com.google.common.base.Predicate<Entity>) entity1 -> entity1.isAlive() && !entity1.isSpectator());
    }
}
