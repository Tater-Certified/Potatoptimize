package com.github.tatercertified.potatoptimize.mixin.threading.world_thread_safe;

import com.github.tatercertified.potatoptimize.interfaces.WorldInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Mixin(World.class)
public abstract class WorldSafeGuardMixin implements WorldInterface, WorldAccess {

    @Shadow @Final public boolean isClient;

    @Shadow public abstract boolean isDebugWorld();

    @Shadow public abstract WorldChunk getWorldChunk(BlockPos pos);

    @Shadow public abstract BlockState getBlockState(BlockPos pos);
    @Shadow public abstract void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4);

    @Shadow public abstract void updateNeighborsAlways(BlockPos pos, Block sourceBlock);

    @Shadow public abstract void updateComparators(BlockPos pos, Block block);

    @Shadow public abstract void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock);

    @Unique
    private ExecutorService executorService;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(MutableWorldProperties properties, RegistryKey registryRef, DynamicRegistryManager registryManager, RegistryEntry dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void submitRunnableToExecutor(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    private void cursedCarpetCompat(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isClient) {
            this.executorService.submit(() -> {
                if (this.isOutOfHeightLimit(pos)) {
                    cir.setReturnValue(false);
                }
                if (this.isDebugWorld()) {
                    cir.setReturnValue(false);
                }
                WorldChunk worldChunk = this.getWorldChunk(pos);
                Block block = state.getBlock();
                BlockState blockState = worldChunk.setBlockState(pos, state, (flags & Block.MOVED) != 0);
                if (blockState != null) {
                    BlockState blockState2 = this.getBlockState(pos);
                    if (blockState2 == state) {
                        if ((flags & Block.NOTIFY_LISTENERS) != 0 && ((flags & Block.NO_REDRAW) == 0) && (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkLevelType.BLOCK_TICKING))) {
                            this.updateListeners(pos, blockState, state, flags);
                        }
                        if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
                            this.updateNeighborsAlways(pos, blockState.getBlock());
                            if (state.hasComparatorOutput()) {
                                this.updateComparators(pos, block);
                            }
                        }
                        if ((flags & Block.FORCE_STATE) == 0 && maxUpdateDepth > 0) {
                            int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
                            blockState.prepare((World) (Object) this, pos, i, maxUpdateDepth - 1);
                            state.updateNeighbors((World) (Object) this, pos, i, maxUpdateDepth - 1);
                            state.prepare((World) (Object) this, pos, i, maxUpdateDepth - 1);
                        }
                        this.onBlockChanged(pos, blockState, blockState2);
                    }
                    cir.setReturnValue(true);
                }
                cir.setReturnValue(false);
            });
        }
    }
}
