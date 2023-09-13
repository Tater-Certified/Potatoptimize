package com.github.tatercertified.potatoptimize.mixin.threading.entity_ticking;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityTestMixin {
    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickPortal()V"))
    private void test1(CallbackInfo ci) {
        System.out.println("TEST 1");
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateWaterState()Z"))
    private void test2(CallbackInfo ci) {
        System.out.println("TEST 2");
    }


    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateSubmergedInWaterState()V"))
    private void test3(CallbackInfo ci) {
        System.out.println("TEST 3");
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateSwimming()V"))
    private void test4(CallbackInfo ci) {
        System.out.println("TEST 4");
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;extinguish()V"))
    private void test5(CallbackInfo ci) {
        System.out.println("TEST 5");
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;attemptTickInVoid()V", shift = At.Shift.AFTER))
    private void test6(CallbackInfo ci) {
        System.out.println("TEST 6");
    }
}
