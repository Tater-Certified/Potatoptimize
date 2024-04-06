package com.github.tatercertified.potatoptimize.mixin.block_entity.sign_ticking;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin {

    @Redirect(method = "getTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractSignBlock;checkType(Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/block/entity/BlockEntityTicker;)Lnet/minecraft/block/entity/BlockEntityTicker;"))
    private <T extends BlockEntity> BlockEntityTicker<T> removeTicker(BlockEntityType<T> blockEntityType, BlockEntityType<T> blockEntityType1, BlockEntityTicker<T> blockEntityTicker) {
        return null;
    }
}
