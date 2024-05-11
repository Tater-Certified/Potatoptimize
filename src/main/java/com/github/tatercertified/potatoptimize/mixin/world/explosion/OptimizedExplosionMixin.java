package com.github.tatercertified.potatoptimize.mixin.world.explosion;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
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
@Mixin(Explosion.class)
public class OptimizedExplosionMixin {
    @Shadow @Final private World world;

    public OptimizedExplosionMixin() {
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState redirectFluidState(World instance, BlockPos pos, @Local(ordinal = 0) BlockState blockState) {
        return blockState.getFluidState();
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
    private List<Entity> optimizeGetEntities(World instance, Entity entity, Box box, @Local(ordinal = 0) int k, @Local(ordinal = 0) int r, @Local(ordinal = 0) int t, @Local(ordinal = 0) int l, @Local(ordinal = 0) int s, @Local(ordinal = 0) int u) {
        return this.world.getOtherEntities(entity, new Box(k, r, t, l, s, u), (com.google.common.base.Predicate<Entity>) entity1 -> entity1.isAlive() && !entity1.isSpectator());
    }
}
