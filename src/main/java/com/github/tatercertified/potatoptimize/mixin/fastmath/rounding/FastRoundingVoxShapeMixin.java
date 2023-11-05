package com.github.tatercertified.potatoptimize.mixin.fastmath.rounding;

import com.github.tatercertified.potatoptimize.utils.math.FasterMathUtil;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = VoxelShapes.class, priority = 1010)
public class FastRoundingVoxShapeMixin {
    @Redirect(
            method = "cuboidUnchecked(DDDDDD)Lnet/minecraft/util/shape/VoxelShape;",
            require = 0,
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(D)J"))
    private static long fasterRoundCuboid(double value) {
        return FasterMathUtil.round(value);
    }

    @Redirect(
            method = "findRequiredBitResolution(DD)I",
            require = 0,
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(D)J"))
    private static long fasterRoundResolution(double value) {
        return FasterMathUtil.round(value);
    }
}
