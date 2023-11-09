package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.ArrayConstants;
import net.minecraft.nbt.NbtByteArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtByteArray.class)
public class NbtByteArrayMixin {
    @Shadow private byte[] value;

    /**
     * @author QPCrummer
     * @reason Reduce allocations
     */
    @Overwrite
    public void clear() {
        this.value = ArrayConstants.emptyByteArray;
    }
}
