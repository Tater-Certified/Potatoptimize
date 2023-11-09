package com.github.tatercertified.potatoptimize.mixin.entity.speed;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Credit Mirai patch #0046
@Mixin(Entity.class)
public abstract class EntitySpeedMixin {
    @Shadow public abstract Vec3d getVelocity();

    @Shadow protected abstract float getVelocityMultiplier();

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocityMultiplier()F"))
    private float redirectMultiplier(Entity instance) {
        if (this.getVelocity().x == 0 && this.getVelocity().z == 0) {
            return 1;
        } else {
            return this.getVelocityMultiplier();
        }
    }
}
