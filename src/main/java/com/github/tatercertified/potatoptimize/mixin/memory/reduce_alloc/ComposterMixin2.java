package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import net.minecraft.block.ComposterBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ComposterBlock.DummyInventory.class)
public class ComposterMixin2 {
    /**
     * @author QPCrummer
     * @reason Reduce Allocations
     */
    @Overwrite
    public int[] getAvailableSlots(Direction side) {
        return Constants.emptyIntArray;
    }
}
