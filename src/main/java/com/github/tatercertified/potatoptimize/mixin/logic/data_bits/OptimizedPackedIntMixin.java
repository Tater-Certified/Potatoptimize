package com.github.tatercertified.potatoptimize.mixin.logic.data_bits;

import net.minecraft.util.collection.PackedIntegerArray;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Credit to PaperMC patch #0087
 */
@Mixin(PackedIntegerArray.class)
public class OptimizedPackedIntMixin {
    @Shadow @Final private int indexScale;
    @Shadow @Final private int indexOffset;
    @Shadow @Final private int indexShift;
    @Unique
    @Final @Mutable
    private long indexScaleUnsigned;
    @Unique
    @Final @Mutable
    private long indexOffsetUnsigned;

    @Inject(method = "<init>(II[J)V", at = @At(value = "FIELD", target = "Lnet/minecraft/util/collection/PackedIntegerArray;indexOffset:I"))
    private void addUnsigned(int elementBits, int size, long[] data, CallbackInfo ci) {
        this.indexScaleUnsigned = Integer.toUnsignedLong(this.indexScale);
    }

    @Inject(method = "<init>(II[J)V", at = @At(value = "FIELD", target = "Lnet/minecraft/util/collection/PackedIntegerArray;indexShift:I"))
    private void addUnsigned2(int elementBits, int size, long[] data, CallbackInfo ci) {
        this.indexOffsetUnsigned = Integer.toUnsignedLong(this.indexOffset);
    }

    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    private int getStorageIndex(int index) {
        return (int) ((long) index * this.indexScaleUnsigned + this.indexOffsetUnsigned >> 32 >> this.indexShift);
    }
}
