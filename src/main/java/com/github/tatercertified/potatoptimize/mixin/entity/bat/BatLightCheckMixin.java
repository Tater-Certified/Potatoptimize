package com.github.tatercertified.potatoptimize.mixin.entity.bat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.minecraft.entity.mob.MobEntity.canMobSpawn;

@IfModAbsent(value = "nocturnal-bats")
@Mixin(value = BatEntity.class)
public class BatLightCheckMixin {
    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;getLightLevel(Lnet/minecraft/util/math/BlockPos;)I"))
    private static int removeLightCheck(WorldAccess instance, BlockPos blockPos) {
        return 0;
    }

    @ModifyReturnValue(method = "canSpawn", at = @At("RETURN"))
    private static boolean readdLightCheck(boolean original, @Local(ordinal = 0, argsOnly = true) EntityType<BatEntity> type, @Local(ordinal = 0, argsOnly = true) WorldAccess world, @Local(ordinal = 0, argsOnly = true) SpawnReason spawnReason, @Local(ordinal = 0, argsOnly = true) BlockPos pos, @Local(ordinal = 0, argsOnly = true) Random random, @Local(ordinal = 1) int j) {
        return world.getLightLevel(pos) <= random.nextInt(j) && canMobSpawn(type, world, spawnReason, pos, random);
    }
}
