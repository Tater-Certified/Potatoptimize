package com.github.tatercertified.potatoptimize.mixin.entity.spawning.slime;

import com.github.tatercertified.potatoptimize.utils.interfaces.SlimeChunkInterface;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This optimization caches slime chunks inside the chunk object. This prevents having to calculate the random
 * value every slime spawn attempt, but instead just doing a chunk lookup.
 * This optimization has vanilla parity.
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(Slime.class)
public abstract class RandomSlimeEntityMixin extends Mob implements Enemy {
    protected RandomSlimeEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "checkSlimeSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void injectQuickReturn(EntityType<Slime> entityType, LevelAccessor world, EntitySpawnReason entitySpawnReason, BlockPos blockPos, RandomSource randomSource, CallbackInfoReturnable<Boolean> cir) {
        if (!(world instanceof WorldGenLevel)) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "checkSlimeSpawnRules", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean reorganizeSurfaceSpawning(Holder<Biome> instance, TagKey<Biome> tTagKey, @Local(ordinal = 0, argsOnly = true) EntityType<Slime> type, @Local(ordinal = 0, argsOnly = true) LevelAccessor world, @Local(ordinal = 0, argsOnly = true) EntitySpawnReason spawnReason, @Local(ordinal = 0, argsOnly = true) BlockPos pos, @Local(ordinal = 0, argsOnly = true) RandomSource random) {
        if (pos.getY() < 70 && pos.getY() > 50 && instance.is(tTagKey)) {
            float randomFloat = random.nextFloat(); // This will slightly change parity
            if (randomFloat > 0.5F && randomFloat > world.getMoonBrightness() && world.getMaxLocalRawBrightness(pos) <= random.nextInt(8)) {
                return checkMobSpawnRules(type, world, spawnReason, pos, random);
            }
        }
        return false;
    }


    @WrapOperation(method = "checkSlimeSpawnRules", constant = @Constant(classValue = WorldGenLevel.class, ordinal = 0))
    private static boolean removeInstanceOfCheck(Object world, Operation<Boolean> original) {
        return true;
    }

    @Inject(method = "checkSlimeSpawnRules", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ChunkPos;<init>(Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    private static void simplifySlimeChunkCheck(EntityType<Slime> type, LevelAccessor world, EntitySpawnReason entitySpawnReason, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (pos.getY() < 40 && random.nextInt(10) == 0) {
            LevelChunk chunk = (LevelChunk) world.getChunk(pos);
            if (((SlimeChunkInterface)chunk).isSlimeChunk()) {
                cir.setReturnValue(canSlimeSpawn(pos, world, type));
                return;
            }
        }

        // Cancels vanilla logic afterwards
        cir.setReturnValue(false);
    }

    private static boolean canSlimeSpawn(BlockPos pos, LevelReader world, EntityType<Slime> type) {
        pos = pos.below();
        return world.getBlockState(pos).isValidSpawn(world, pos, type);
    }
}
