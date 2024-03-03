package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import net.minecraft.nbt.NbtLongArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtLongArray.class)
public class NbtLongArrayMixin {
    @Shadow
    private long[] value;

    /**
     * @author QPCrummer
     * @reason Reduce Allocations
     */
    @Overwrite
    public void clear() {
        this.value = Constants.emptyLongArray;
    }
}
