package com.github.tatercertified.potatoptimize.mixin.block_entity.chest_chunk_loading;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Credit to PaperMC patch #0331
 */
@Mixin(DoubleBlockProperties.class)
public class NoChestChunkLoadingMixin {
    @Redirect(method = "toPropertySource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private static <S extends BlockEntity> BlockState noChunkLoading(WorldAccess instance, BlockPos pos, @Local(ordinal = 0) S blockEntity) {
        if (instance.isChunkLoaded(pos)) {
            return instance.getBlockState(pos);
        }
        return null;
    }

    @Inject(method = "toPropertySource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.AFTER), cancellable = true)
    private static <S extends BlockEntity> void checkIfNull(BlockEntityType<S> blockEntityType, Function<BlockState, DoubleBlockProperties.Type> typeMapper, Function<BlockState, Direction> function, DirectionProperty directionProperty, BlockState state, WorldAccess world, BlockPos pos, BiPredicate<WorldAccess, BlockPos> fallbackTester, CallbackInfoReturnable<DoubleBlockProperties.PropertySource<S>> cir, @Local(ordinal = 0) BlockState blockState, @Local(ordinal = 0) S blockEntity) {
        if (blockState == null) {
            cir.setReturnValue(new DoubleBlockProperties.PropertySource.Single<>(blockEntity));
        }
    }
}
