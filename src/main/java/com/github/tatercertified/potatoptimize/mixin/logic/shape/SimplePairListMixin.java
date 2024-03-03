package com.github.tatercertified.potatoptimize.mixin.logic.shape;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.SimplePairList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Credit: PaperMC patch #1003
 */
@Mixin(SimplePairList.class)
public class SimplePairListMixin {
    @Mutable @Shadow @Final private double[] valueIndices;
    @Mutable @Shadow @Final private int size;
    @Mutable @Shadow @Final private int[] minValues;
    @Mutable @Shadow @Final private int[] maxValues;
    private static final int[] INFINITE_B_1 = new int[]{1, 1};
    private static final int[] INFINITE_B_0 = new int[]{0, 0};
    private static final int[] INFINITE_C = new int[]{0, 1};

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/shape/SimplePairList;valueIndices:[D", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void injectIntoConstructor(DoubleList first, DoubleList second, boolean includeFirstOnly, boolean includeSecondOnly, CallbackInfo ci, @Local(ordinal = 0) int i) {
        double tail = first.getDouble(i - 1);
        double head = first.getDouble(0);
        if (head == Double.NEGATIVE_INFINITY && tail == Double.POSITIVE_INFINITY && !includeFirstOnly && !includeSecondOnly && (i == 2 || i == 4)) {
            this.valueIndices = second.toDoubleArray();
            this.size = second.size();
            if (i == 2) {
                this.minValues = INFINITE_B_0;
            } else {
                this.minValues = INFINITE_B_1;
            }
            this.maxValues = INFINITE_C;
            ci.cancel();
        }
    }
}
