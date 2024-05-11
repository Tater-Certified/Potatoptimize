package com.github.tatercertified.potatoptimize.mixin.entity.villager;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Credit to PaperMC patch #0326
 */
@Mixin(SleepTask.class)
public class VillagerNoChunkLoadingMixin {
    @Redirect(method = "shouldRun", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState noChunkLoading(ServerWorld instance, BlockPos pos) {
        return instance.isChunkLoaded(pos) ? instance.getBlockState(pos) : null;
    }
}
