package com.github.tatercertified.potatoptimize.mixin.threading.entity_ticking;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityTestMixin {

    // TODO This causes issues
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateSubmergedInWaterState()V"))
    private void test1(Entity instance) {
    }

    // TODO This causes issues
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateWaterState()Z"))
    private boolean test2(Entity instance) {
        return false;
    }

}
