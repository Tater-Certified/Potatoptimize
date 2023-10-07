package com.github.tatercertified.potatoptimize.mixin.entity.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Credit to PaperMC patch #0326
 */
@Mixin(SleepTask.class)
public class VillagerNoChunkLoadingMixin {
    @Redirect(method = "shouldRun", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState noChunkLoading(ServerWorld instance, BlockPos pos) {
        return instance.isChunkLoaded(pos) ? instance.getBlockState(pos) : null;
    }

    @Inject(method = "shouldRun", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/GlobalPos;getPos()Lnet/minecraft/util/math/BlockPos;", ordinal = 1), cancellable = true)
    private void checkForNull(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) BlockState blockState) {
        if (blockState == null) {
            cir.setReturnValue(false);
        }
    }
}
