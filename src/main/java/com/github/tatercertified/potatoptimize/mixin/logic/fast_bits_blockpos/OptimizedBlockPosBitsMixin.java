package com.github.tatercertified.potatoptimize.mixin.logic.fast_bits_blockpos;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Credit to PaperMC Patch #0421
 */
@Mixin(BlockPos.class)
public class OptimizedBlockPosBitsMixin {

    /**
     * @author QPCrummer
     * @reason Inline
     */
    @Overwrite
    public static long add(long value, int x, int y, int z) {
        return asLong((int) (value >> 38) + x, (int) ((value << 52) >> 52) + y, (int) ((value << 26) >> 38) + z);
    }

    /**
     * @author QPCrummer
     * @reason Inline
     */
    @Overwrite
    public static int unpackLongX(long packedPos) {
        return (int) (packedPos >> 38);
    }

    /**
     * @author QPCrummer
     * @reason Inline
     */
    @Overwrite
    public static int unpackLongY(long packedPos) {
        return (int) ((packedPos << 52) >> 52);
    }

    /**
     * @author QPCrummer
     * @reason Inline
     */
    @Overwrite
    public static int unpackLongZ(long packedPos) {
        return (int) ((packedPos << 26) >> 38);
    }

    /**
     * @author QPCrummer
     * @reason Inline
     */
    @Overwrite
    public static BlockPos fromLong(long packedPos) {
        return new BlockPos((int) (packedPos >> 38), (int) ((packedPos << 52) >> 52), (int) ((packedPos << 26) >> 38));
    }

    /**
     * @author QPCrummer
     * @reason Inline
     */
    @Overwrite
    public static long asLong(int x, int y, int z) {
        return (((long) x & (long) 67108863) << 38) | (((long) y & (long) 4095)) | (((long) z & (long) 67108863) << 12);
    }

}
