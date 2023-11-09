package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.ArrayConstants;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NbtCompound.class)
public class NbtCompoundMixin {

    @ModifyReturnValue(method = "getIntArray", at = @At(value = "RETURN", ordinal = 1))
    private int[] redirectGetIntArray(int[] original) {
        return ArrayConstants.emptyIntArray;
    }

    @ModifyReturnValue(method = "getLongArray", at = @At(value = "RETURN", ordinal = 1))
    private long[] redirectGetLongArray(long[] original) {
        return ArrayConstants.emptyLongArray;
    }

    @ModifyReturnValue(method = "getByteArray", at = @At(value = "RETURN", ordinal = 1))
    private byte[] redirectGetByteArray(byte[] original) {
        return ArrayConstants.emptyByteArray;
    }
}
