package com.github.tatercertified.potatoptimize.mixin.entity.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Credit to PaperMC patch #0371
 */
@Mixin(EntityNavigation.class)
public abstract class MobPathfindingMixin {

    @Shadow @Nullable protected Path currentPath;

    @Shadow public abstract boolean startMovingAlong(@Nullable Path path, double speed);

    @Shadow @Final protected MobEntity entity;
    @Unique
    private int lastFailure = 0;
    @Unique
    private int pathfindFailures = 0;

    @Inject(method = "startMovingTo(Lnet/minecraft/entity/Entity;D)Z", at = @At("HEAD"), cancellable = true)
    private void startMovingTo(Entity entity, double speed, CallbackInfoReturnable<Boolean> cir) {
        if (this.pathfindFailures > 10 && this.currentPath == null && entity.getServer().getTicks() < this.lastFailure + 40) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "startMovingTo(Lnet/minecraft/entity/Entity;D)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;startMovingAlong(Lnet/minecraft/entity/ai/pathing/Path;D)Z"))
    private boolean insertCancellation(EntityNavigation instance, Path path, double speed) {
        if (this.startMovingAlong(path, speed)) {
            this.lastFailure = 0;
            this.pathfindFailures = 0;
            return true;
        } else {
            this.pathfindFailures++;
            this.lastFailure = this.entity.getServer().getTicks();
            return false;
        }
    }
}
