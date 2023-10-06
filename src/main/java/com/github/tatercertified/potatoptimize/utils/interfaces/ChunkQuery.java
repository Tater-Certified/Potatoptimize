package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.world.chunk.WorldChunk;

public interface ChunkQuery {
    WorldChunk getChunkIfLoaded(int x, int y);
}
