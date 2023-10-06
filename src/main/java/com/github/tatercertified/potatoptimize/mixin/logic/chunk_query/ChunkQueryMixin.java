package com.github.tatercertified.potatoptimize.mixin.logic.chunk_query;

import com.github.tatercertified.potatoptimize.utils.interfaces.ChunkQuery;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class ChunkQueryMixin implements ChunkQuery, WorldAccess {

    @Override
    public WorldChunk getChunkIfLoaded(int x, int y) {
        return this.isChunkLoaded(x, y) ? (WorldChunk) this.getChunk(x, y) : null;

    }
}
