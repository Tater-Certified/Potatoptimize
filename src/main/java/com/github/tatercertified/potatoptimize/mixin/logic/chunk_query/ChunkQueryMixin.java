package com.github.tatercertified.potatoptimize.mixin.logic.chunk_query;

import com.github.tatercertified.potatoptimize.utils.interfaces.ChunkQuery;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class ChunkQueryMixin implements ChunkQuery, WorldAccess, AutoCloseable {

    @Override
    public WorldChunk getChunkIfLoaded(int x, int y) {
        return this.isChunkLoaded(x, y) ? (WorldChunk) this.getChunk(x, y) : null;
    }

    @Override
    public WorldChunk getChunkIfLoaded(BlockPos pos) {
        return this.isChunkLoaded(pos.getX(), pos.getZ()) ? (WorldChunk) this.getChunk(pos) : null;
    }

    @Override
    public boolean isChunkNearby(BlockPos pos1, BlockPos pos2, int limit) {
        return pos1.isWithinDistance(pos2, limit);
    }

    @Override
    public WorldChunk getChunkIfNearAndLoaded(BlockPos entityPos, BlockPos chunkPos, int limit) {
        return this.isChunkNearby(entityPos, chunkPos, limit) ? this.getChunkIfLoaded(chunkPos) : null;
    }
}
