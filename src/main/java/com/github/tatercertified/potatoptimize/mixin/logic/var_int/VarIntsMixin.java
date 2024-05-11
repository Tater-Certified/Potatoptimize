package com.github.tatercertified.potatoptimize.mixin.logic.var_int;

import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.encoding.VarInts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

// Credit to PaperMC patch #1042
@IfModAbsent(value = "krypton")
@Mixin(VarInts.class)
public class VarIntsMixin {
    /**
     * @author QPCrummer
     * @reason Optimize with exact lengths
     */
    @Overwrite
    public static int getSizeInBytes(int i) {
        return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(i)];
    }

    @Unique
    private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];
    static {
        for (int i = 0; i <= 32; ++i) {
            VARINT_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((31d - (i - 1)) / 7d);
        }
        VARINT_EXACT_BYTE_LENGTHS[32] = 1;
    }

    /**
     * @author QPCrummer
     * @reason Improve inlining
     */
    @Overwrite
    public static ByteBuf write(ByteBuf buf, int i) {
        if ((i & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(i);
        } else if ((i & (0xFFFFFFFF << 14)) == 0) {
            int w = (i & 0x7F | 0x80) << 8 | (i >>> 7);
            buf.writeShort(w);
        } else {
            writeOld(buf, i);
        }
        return buf;
    }

    @Unique
    private static void writeOld(ByteBuf buf, int i) {
        while((i & -128) != 0) {
            buf.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        buf.writeByte(i);
    }
}
