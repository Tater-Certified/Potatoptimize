package com.github.tatercertified.potatoptimize.mixin.entity.bee;

import com.github.tatercertified.potatoptimize.utils.interfaces.ChunkQuery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Inject(method = "isHiveNearFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"), cancellable = true)
    private void cancelIfNotLoaded(CallbackInfoReturnable<Boolean> cir) {
        if (!((ChunkQuery)(this.getWorld())).isChunkNearby(this.getBlockPos(), this.getHivePos(), 32) || !this.getWorld().isChunkLoaded(this.getHivePos())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "doesHiveHaveSpace", at = @At("HEAD"), cancellable = true)
    private void cancelIfNotLoaded(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!((ChunkQuery)(this.getWorld())).isChunkNearby(this.getBlockPos(), this.getHivePos(), 32) || !this.getWorld().isChunkLoaded(this.getHivePos())) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "isHiveValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;isTooFar(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean cancelIfNotLoaded(BeeEntity instance, BlockPos pos) {
        return !((ChunkQuery)(this.getWorld())).isChunkNearby(this.getBlockPos(), this.getHivePos(), 32) || !this.getWorld().isChunkLoaded(this.getHivePos());
    }
}
