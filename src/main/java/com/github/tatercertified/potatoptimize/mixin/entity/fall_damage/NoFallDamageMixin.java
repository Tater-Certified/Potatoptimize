package com.github.tatercertified.potatoptimize.mixin.entity.fall_damage;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class NoFallDamageMixin {

    @Shadow public abstract boolean isOnGround();

    @Shadow public abstract void onLanding();


    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    private void cancelFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        if (!(((Entity)(Object)this) instanceof LivingEntity)) {
            if (this.isOnGround()) {
                this.onLanding();
            }
            ci.cancel();
        }
    }

}
