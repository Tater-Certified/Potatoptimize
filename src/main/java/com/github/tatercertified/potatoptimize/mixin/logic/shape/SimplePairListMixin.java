package com.github.tatercertified.potatoptimize.mixin.logic.shape;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.SimplePairList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/shape/SimplePairList;valueIndices:[D", ordinal = 0, shift = At.Shift.BEFORE))
    private void injectIntoConstructor(DoubleList first, DoubleList second, boolean includeFirstOnly, boolean includeSecondOnly, CallbackInfo ci, @Local(ordinal = 0) int i, @Share("arg") LocalBooleanRef booleanRef) {
        double tail = first.getDouble(i - 1);
        double head = first.getDouble(0);
        boolean process = head == Double.NEGATIVE_INFINITY && tail == Double.POSITIVE_INFINITY && !includeFirstOnly && !includeSecondOnly && (i == 2 || i == 4);
        booleanRef.set(process);
        if (process) {
            this.valueIndices = second.toDoubleArray();
            this.size = second.size();
            if (i == 2) {
                this.minValues = INFINITE_B_0;
            } else {
                this.minValues = INFINITE_B_1;
            }
            this.maxValues = INFINITE_C;
        }
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 1))
    private static int injected(int constant, @Share("arg") LocalBooleanRef booleanRef) {
        if (booleanRef.get()) {
            return 0;
        } else {
            return 1;
        }
    }
}
