package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkQuery {
    WorldChunk getChunkIfLoaded(int x, int y);
    WorldChunk getChunkIfLoaded(BlockPos pos);
    boolean isChunkNearby(BlockPos pos1, BlockPos pos2, int limit);
    WorldChunk getChunkIfNearAndLoaded(BlockPos pos1, BlockPos pos2, int limit);
}
