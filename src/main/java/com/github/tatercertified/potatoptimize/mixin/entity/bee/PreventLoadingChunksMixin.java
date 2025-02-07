package com.github.tatercertified.potatoptimize.mixin.entity.bee;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Credit to PaperMC patch #0363
 */
@Mixin(BeeEntity.class)
public abstract class PreventLoadingChunksMixin extends Entity {

    @Shadow @Nullable public abstract BlockPos getHivePos();

    public PreventLoadingChunksMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getHive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntityType;)Ljava/util/Optional;"), cancellable = true)
    private void cancelIfNotLoaded(CallbackInfoReturnable<Boolean> cir) {
        if (this.isChunkNotNear(this.getBlockPos(), this.getHivePos()) || !this.getWorld().isChunkLoaded(this.getHivePos())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "doesHiveHaveSpace", at = @At("HEAD"), cancellable = true)
    private void cancelIfNotLoaded(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.isChunkNotNear(this.getBlockPos(), pos) || !this.getWorld().isChunkLoaded(pos)) {
            cir.setReturnValue(false);
        }
    }

    // TODO Make this work with ChunkQuery
    @Unique
    private boolean isChunkNotNear(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null) {
            return false;
        }
        return !pos1.isWithinDistance(pos2, 32);
    }
}
