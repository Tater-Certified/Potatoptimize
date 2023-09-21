package com.github.tatercertified.potatoptimize.mixin.threading.thread_safe_chunks;

import com.github.tatercertified.potatoptimize.utils.threading.ThreadedTaskExecutor;
import com.mojang.datafixers.util.Either;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkManager.class)
public abstract class ChunkAccessMixin {

    @Shadow
    public abstract @Nullable Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create);

    @Shadow @Final
    ServerWorld world;

    @Shadow @Final private long[] chunkPosCache;

    @Shadow @Final private ChunkStatus[] chunkStatusCache;

    @Shadow @Final private Chunk[] chunkCache;

    @Shadow protected abstract CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

    @Shadow protected abstract void putInCache(long pos, Chunk chunk, ChunkStatus status);

    @Unique
    private Chunk threadSafeGetChunk(int x, int z, ChunkStatus leastStatus, boolean create, Object executor) {
        if (executor instanceof ThreadedTaskExecutor) {
            Chunk chunk2;
            Profiler profiler = this.world.getProfiler();
            profiler.visit("getChunk");
            long l = ChunkPos.toLong(x, z);
            for (int i = 0; i < 4; ++i) {
                if (l != this.chunkPosCache[i] || leastStatus != this.chunkStatusCache[i] || (chunk2 = this.chunkCache[i]) == null && create) continue;
                return chunk2;
            }
            profiler.visit("getChunkCacheMiss");
            CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
            ((ThreadedTaskExecutor) executor).executeThreadedFuture(completableFuture);
            chunk2 = completableFuture.join().map(chunk -> chunk, unloaded -> {
                if (create) {
                    throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
                }
                return null;
            });
            this.putInCache(l, chunk2, leastStatus);
            return chunk2;
        } else {
            return this.getChunk(x, z, leastStatus, create);
        }
    }

    
}
