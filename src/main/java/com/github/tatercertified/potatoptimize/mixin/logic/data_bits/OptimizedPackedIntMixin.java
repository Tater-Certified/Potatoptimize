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
    @Shadow @Final private long[] data;
    @Shadow @Final private int elementsPerLong;
    @Shadow @Final private int elementBits;
    @Shadow @Final private long maxValue;
    @Final @Mutable
    private long indexScaleUnsigned;
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

    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    @Final
    public int swap(int index, int value) {
        int i = this.getStorageIndex(index);
        long l = this.data[i];
        int j = (index - i * this.elementsPerLong) * this.elementBits;
        int k = (int)(l >> j & this.maxValue);
        this.data[i] = l & ~(this.maxValue << j) | ((long)value & this.maxValue) << j;
        return k;
    }

    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    @Final
    public void set(int index, int value) {
        int i = this.getStorageIndex(index);
        long l = this.data[i];
        int j = (index - i * this.elementsPerLong) * this.elementBits;
        this.data[i] = l & ~(this.maxValue << j) | ((long)value & this.maxValue) << j;
    }

    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    @Final
    public int get(int index) {
        int i = this.getStorageIndex(index);
        long l = this.data[i];
        int j = (index - i * this.elementsPerLong) * this.elementBits;
        return (int)(l >> j & this.maxValue);
    }


}
