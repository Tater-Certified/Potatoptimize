/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.blockentity.ticking.sign;

import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignBlock.class)
public class AbstractSignBlockMixin {

    @Redirect(
            method = "getTicker",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/block/SignBlock;createTickerHelper(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/world/level/block/entity/BlockEntityTicker;)Lnet/minecraft/world/level/block/entity/BlockEntityTicker;"))
    private <T extends BlockEntity> BlockEntityTicker<T> removeTicker(
            BlockEntityType<T> blockEntityType,
            BlockEntityType<T> blockEntityType1,
            BlockEntityTicker<T> blockEntityTicker) {
        return null;
    }
}
