package com.github.tatercertified.potatoptimize.mixin.player.stats;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This optimization makes player stats only update every second rather than every tick.
 * This will break vanilla parity.
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(Player.class)
public abstract class PlayerEntityStatsMixin extends LivingEntity {

    @Shadow protected FoodData foodData;

    @Shadow public abstract void awardStat(ResourceLocation resourceLocation, int i);

    protected PlayerEntityStatsMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private int count;

    @WrapOperation(method = "tick", constant = @Constant(classValue = ServerPlayer.class, ordinal = 0))
    private boolean slowIncrementStat(Object player, Operation<Boolean> original) {
        count++;
        if (count == 19) {
            count = 0;
            return original.call(player);
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                this.foodData.tick(serverPlayer);
            }
            return false;
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"))
    private void add20(Player instance, ResourceLocation resourceLocation) {
        this.awardStat(resourceLocation, 20);
    }
}
