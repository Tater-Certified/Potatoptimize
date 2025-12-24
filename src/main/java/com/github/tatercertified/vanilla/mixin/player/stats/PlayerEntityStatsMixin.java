/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.player.stats;

import com.mojang.authlib.GameProfile;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This optimization makes player stats only update every second rather than every tick. This will
 * break vanilla parity.
 *
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(ServerPlayer.class)
public abstract class PlayerEntityStatsMixin extends Player {
    @Unique private int count;
    private boolean shouldIncrementStats;

    public PlayerEntityStatsMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void potatoptimize$checkForStatsTick(CallbackInfo ci) {
        this.count++;
        if (this.count == 19) {
            this.count = 0;
            this.shouldIncrementStats = true;
        }
    }

    @Redirect(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/Identifier;)V"))
    private void potatoptimize$delayStat(ServerPlayer instance, Identifier resourceLocation) {
        if (this.shouldIncrementStats) {
            this.awardStat(resourceLocation, 20);
        }
    }

    @Inject(
            method = "doTick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/player/Inventory;getContainerSize()I"))
    private void potatoptimize$resetIncrementBool(CallbackInfo ci) {
        this.shouldIncrementStats = false;
    }
}
