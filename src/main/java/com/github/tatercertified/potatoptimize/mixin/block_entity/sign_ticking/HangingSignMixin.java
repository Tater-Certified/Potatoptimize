package com.github.tatercertified.potatoptimize.mixin.block_entity.sign_ticking;

import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HangingSignBlock.class)
public class HangingSignMixin {

    @Redirect(method = "getTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/HangingSignBlock;validateTicker(Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/block/entity/BlockEntityTicker;)Lnet/minecraft/block/entity/BlockEntityTicker;"))
    private BlockEntityTicker removeTicker(BlockEntityType blockEntityType, BlockEntityType blockEntityType1, BlockEntityTicker blockEntityTicker) {
        return null;
    }
}
