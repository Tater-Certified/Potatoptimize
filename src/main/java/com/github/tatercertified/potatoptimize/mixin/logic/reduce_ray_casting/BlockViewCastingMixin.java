package com.github.tatercertified.potatoptimize.mixin.logic.reduce_ray_casting;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Credit to PaperMC Patch #0687 and #684
 */

@Mixin(BlockView.class)
public interface BlockViewCastingMixin extends HeightLimitView {
    @Shadow BlockState getBlockState(BlockPos pos);

    @Shadow @Nullable BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state);

    @Shadow static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory){return null;}

    /**
     * @author QPCrummer
     * @reason Optimize
     */
    @Overwrite
    default BlockHitResult raycast(RaycastContext context) {
        return raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
            BlockState blockState = this.getBlockState(pos);
            if (blockState.isAir()) return null;
            FluidState fluidState = blockState.getFluidState();
            Vec3d vec3d = innerContext.getStart();
            Vec3d vec3d2 = innerContext.getEnd();
            VoxelShape voxelShape = innerContext.getBlockShape(blockState, (BlockView)(Object)this, pos);
            BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, pos, voxelShape, blockState);
            VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, (BlockView)(Object)this, pos);
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, pos);
            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, (innerContext) -> {
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), ofFloored(innerContext.getEnd()));
        });
    }

    private BlockPos ofFloored(double x, double y, double z) {
        return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    private BlockPos ofFloored(Position pos) {
        return ofFloored(pos.getX(), pos.getY(), pos.getZ());
    }
}
