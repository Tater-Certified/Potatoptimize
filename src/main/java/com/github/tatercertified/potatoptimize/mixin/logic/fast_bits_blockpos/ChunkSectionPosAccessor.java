package com.github.tatercertified.potatoptimize.mixin.logic.fast_bits_blockpos;

import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkSectionPos.class)
public interface ChunkSectionPosAccessor {
    @Invoker("<init>")
    public static ChunkSectionPos invokeChunkSectionPos(int i, int j, int k) {
        throw new AssertionError();
    }
}
