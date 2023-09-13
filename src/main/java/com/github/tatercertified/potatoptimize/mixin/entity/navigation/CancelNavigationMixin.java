package com.github.tatercertified.potatoptimize.mixin.entity.navigation;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(EntityNavigation.class)
public class CancelNavigationMixin {
    @Shadow @Final protected MobEntity entity;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void cancelTick(CallbackInfo ci) {
        if (entity.hasVehicle()) {
            ci.cancel();
        }
    }

    @Inject(method = "findPathToAny(Ljava/util/Set;IZIF)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", shift = At.Shift.AFTER), cancellable = true)
    private void cancelFindPath(Set<BlockPos> positions, int range, boolean useHeadPos, int distance, float followRange, CallbackInfoReturnable<@Nullable Path> cir) {
        if (entity.hasVehicle()) {
            cir.setReturnValue(null);
        }
    }
}
