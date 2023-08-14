package com.github.tatercertified.potatoptimize.mixin.entity.ticking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected abstract void sendEquipmentChanges();

    @Shadow public abstract DamageTracker getDamageTracker();

    @Shadow public abstract boolean isSleeping();

    @Shadow protected abstract boolean isSleepingInBed();

    @Shadow public abstract void wakeUp();

    @Environment(EnvType.SERVER)
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    private boolean redirectGetClient(World instance) {
        if (this.isPlayer()) {
            this.sendEquipmentChanges();
            if (this.age % 20 == 0) {
                this.getDamageTracker().update();
            }

            if (this.isSleeping() && !this.isSleepingInBed()) {
                this.wakeUp();
            }
        }
        return this.isPlayer();
    }
}
