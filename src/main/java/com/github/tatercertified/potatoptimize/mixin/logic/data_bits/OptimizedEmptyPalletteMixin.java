package com.github.tatercertified.potatoptimize.mixin.logic.data_bits;

import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.collection.EmptyPaletteStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@IfModAbsent(value = "modernfix", aliases = {"lithium"})
@Mixin(EmptyPaletteStorage.class)
public class OptimizedEmptyPalletteMixin {
    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    public int swap(int index, int value) {
        return 0;
    }

    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    public void set(int index, int value) {
    }

    /**
     * @author QPCrummer
     * @reason Simplify
     */
    @Overwrite
    public int get(int index) {
        return 0;
    }
}
