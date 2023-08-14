package com.github.tatercertified.potatoptimize.mixin.entity.spawning;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    @Inject(method = "shouldUseNetherFortressSpawns", at = @At("HEAD"), cancellable = true)
    private static void checkDimension(BlockPos pos, ServerWorld world, SpawnGroup spawnGroup, StructureAccessor structureAccessor, CallbackInfoReturnable<Boolean> cir) {
        if (world.getRegistryKey() != ServerWorld.NETHER) {
            cir.setReturnValue(false);
        }
    }

    /**
     * @author QPCrummer
     * @reason There is no reason to check distance to the player twice
     */
    @Overwrite
    private static boolean isAcceptableSpawnPosition(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance) {
        if (squaredDistance <= 576.0) {
            return false;
        }
        return Objects.equals(new ChunkPos(pos), chunk.getPos()) || world.shouldTick(pos);
    }
}
