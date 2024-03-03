package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import net.minecraft.nbt.NbtIntArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtIntArray.class)
public class NbtIntArrayMixin {
    @Shadow private int[] value;

    /**
     * @author QPCrummer
     * @reason Reduce Allocations
     */
    @Overwrite
    public void clear() {
        this.value = Constants.emptyIntArray;
    }
}
