package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NbtCompound.class)
public class NbtCompoundMixin {

    @ModifyReturnValue(method = "getIntArray", at = @At(value = "RETURN", ordinal = 1))
    private int[] redirectGetIntArray(int[] original) {
        return Constants.emptyIntArray;
    }

    @ModifyReturnValue(method = "getLongArray", at = @At(value = "RETURN", ordinal = 1))
    private long[] redirectGetLongArray(long[] original) {
        return Constants.emptyLongArray;
    }

    @ModifyReturnValue(method = "getByteArray", at = @At(value = "RETURN", ordinal = 1))
    private byte[] redirectGetByteArray(byte[] original) {
        return Constants.emptyByteArray;
    }
}
