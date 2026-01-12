/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.vanilla.entity.spawning.slime;

import com.github.tatercertified.vanilla.utils.interfaces.SlimeChunkInterface;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.LevelChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This optimization caches slime chunks inside the chunk object. This prevents having to calculate
 * the random value every slime spawn attempt, but instead just doing a chunk lookup. This
 * optimization has vanilla parity.
 *
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.1.0
 */
@Mixin(Slime.class)
public abstract class RandomSlimeEntityMixin extends Mob implements Enemy {
    protected RandomSlimeEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "checkSlimeSpawnRules",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/ChunkPos;<init>(Lnet/minecraft/core/BlockPos;)V"),
            cancellable = true)
    private static void simplifySlimeChunkCheck(
            EntityType<Slime> type,
            LevelAccessor world,
            EntitySpawnReason entitySpawnReason,
            BlockPos pos,
            RandomSource random,
            CallbackInfoReturnable<Boolean> cir) {
        if (pos.getY() < 40 && random.nextInt(10) == 0) {
            LevelChunk chunk = (LevelChunk) world.getChunk(pos);
            if (((SlimeChunkInterface) chunk).isSlimeChunk()) {
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
