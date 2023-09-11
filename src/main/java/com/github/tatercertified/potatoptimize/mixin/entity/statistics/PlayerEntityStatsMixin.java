package com.github.tatercertified.potatoptimize.mixin.entity.statistics;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityStatsMixin extends LivingEntity {

    @Shadow public abstract void increaseStat(Identifier stat, int amount);

    protected PlayerEntityStatsMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private int count;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"))
    private void slowIncrementStat(PlayerEntity instance, Identifier stat) {
        count++;
        if (count == 19) {
            increaseStat(stat, 20);
            count = 0;
        }
    }
}
