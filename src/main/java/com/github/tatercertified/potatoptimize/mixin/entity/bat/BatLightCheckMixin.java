package com.github.tatercertified.potatoptimize.mixin.entity.bat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BatEntity.class)
public class BatLightCheckMixin {
    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;getLightLevel(Lnet/minecraft/util/math/BlockPos;)I"))
    private static int removeLightCheck(WorldAccess instance, BlockPos blockPos) {
        return 0;
    }

    @Inject(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void readdLightCheck(EntityType<BatEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) int j) {
        if (world.getLightLevel(pos) > random.nextInt(j)) {
            cir.setReturnValue(false);
        }
        cir.setReturnValue(BatEntity.canMobSpawn(type, world, spawnReason, pos, random));
    }
}
