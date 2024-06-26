package com.github.tatercertified.potatoptimize.mixin.unstable.explosion_throttle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntity.class)
public abstract class TntEntityThrottleMixin extends Entity {

    public TntEntityThrottleMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void injectToSeeIfServerIsLagging(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.getServer().getAverageTickTime() > 60) {
            this.discard();
            ci.cancel();
        }
    }
}
