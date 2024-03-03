package com.github.tatercertified.potatoptimize.mixin.logic.shape;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.IdentityPairList;
import net.minecraft.util.shape.PairList;
import net.minecraft.util.shape.SimplePairList;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(VoxelShapes.class)
public abstract class VoxelShapesMixin {
    @Shadow
    protected static native PairList createListPair(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond);

    private static PairList createListPairOptimized(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond) {
        if (first.getDouble(0) == Double.NEGATIVE_INFINITY && first.getDouble(first.size() - 1) == Double.POSITIVE_INFINITY) {
            return new SimplePairList(first, second, includeFirst, includeSecond);
        }
        return createListPair(size, first, second, includeFirst, includeSecond);
    }

    @Redirect(method = "combine", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/shape/VoxelShapes;createListPair(ILit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;ZZ)Lnet/minecraft/util/shape/PairList;"))
    private static PairList redirectListPair1(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond) {
        return createListPairOptimized(size, first, second, includeFirst, includeSecond);
    }

    @Redirect(method = "matchesAnywhere(Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/shape/VoxelShape;Lnet/minecraft/util/function/BooleanBiFunction;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/shape/VoxelShapes;createListPair(ILit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;ZZ)Lnet/minecraft/util/shape/PairList;"))
    private static PairList redirectListPair2(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond) {
        return createListPairOptimized(size, first, second, includeFirst, includeSecond);
    }

    @Inject(method = "createListPair", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/doubles/DoubleList;getDouble(I)D", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private static void injectCreatePairList(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond, CallbackInfoReturnable<PairList> cir, @Local(ordinal = 0) int i, @Local(ordinal = 0) int j) {
        if (i == j && Objects.equals(first, second)) {
            if (first instanceof IdentityPairList) {
                cir.setReturnValue((PairList) first);
            } else if (second instanceof IdentityPairList) {
                cir.setReturnValue((PairList) second);
            }
            cir.setReturnValue(new IdentityPairList(first));
        }
    }

    @Inject(method = "createListPair", at = @At(value = "RETURN"), cancellable = true)
    private static void redirectReturn(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond, CallbackInfoReturnable<PairList> cir) {
        cir.setReturnValue(new SimplePairList(first, second, includeFirst, includeSecond));
    }
}
