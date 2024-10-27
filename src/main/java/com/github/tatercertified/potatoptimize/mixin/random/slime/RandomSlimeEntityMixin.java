package com.github.tatercertified.potatoptimize.mixin.random.slime;

import com.github.tatercertified.potatoptimize.utils.interfaces.SlimeChunkInterface;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeEntity.class)
public abstract class RandomSlimeEntityMixin extends MobEntity implements Monster {
    protected RandomSlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void injectQuickReturn(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (!(world instanceof StructureWorldAccess)) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/SpawnReason;isAnySpawner(Lnet/minecraft/entity/SpawnReason;)Z"))
    private static boolean moveSpawnerCheck(SpawnReason reason) {
        return false;
    }

    // TODO Find a better way to do this
    /*
    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private static boolean reorganizeSurfaceSpawning(RegistryEntry<Biome> instance, TagKey<Biome> tTagKey, @Local(ordinal = 0, argsOnly = true) EntityType<SlimeEntity> type, @Local(ordinal = 0, argsOnly = true) WorldAccess world, @Local(ordinal = 0, argsOnly = true) SpawnReason spawnReason, @Local(ordinal = 0, argsOnly = true) BlockPos pos, @Local(ordinal = 0, argsOnly = true) Random random) {
        if (pos.getY() < 70 && pos.getY() > 50 && instance.isIn(tTagKey)) {
            float randomFloat = random.nextFloat(); // This will slightly change parity
            if (randomFloat > 0.5f && randomFloat > world.getMoonSize() && world.getLightLevel(pos) <= random.nextInt(8)) {
                return canMobSpawn(type, world, spawnReason, pos, random);
            }
        }
        return false;
    }

     */


    @WrapOperation(method = "canSpawn", constant = @Constant(classValue = StructureWorldAccess.class, ordinal = 0))
    private static boolean removeInstanceOfCheck(Object world, Operation<Boolean> original) {
        return true;
    }

    @Inject(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ChunkPos;<init>(Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.BEFORE), cancellable = true)
    private static void simplifySlimeChunkCheck(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (pos.getY() < 40 && random.nextInt(10) == 0) {
            WorldChunk chunk = (WorldChunk) world.getChunk(pos);
            if (((SlimeChunkInterface)chunk).isSlimeChunk()) {
                cir.setReturnValue(canSlimeSpawn(pos, world, type));
                return;
            }
        }

        // Cancels vanilla logic afterwards
        cir.setReturnValue(false);
    }

    private static boolean canSlimeSpawn(BlockPos pos, WorldView world, EntityType<SlimeEntity> type) {
        pos = pos.down();
        return world.getBlockState(pos).allowsSpawning(world, pos, type);
    }
}
