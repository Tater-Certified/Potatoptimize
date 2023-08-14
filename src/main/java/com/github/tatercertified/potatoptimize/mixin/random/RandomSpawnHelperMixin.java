package com.github.tatercertified.potatoptimize.mixin.random;

import com.github.tatercertified.potatoptimize.utils.FastRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnHelper.class)
public class RandomSpawnHelperMixin {
    @Redirect(method = "getRandomPosInChunkSection", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private static int optimizedGetRandomPosInChunkSection(Random instance, int i) {
        return FastRandom.fastRandom.nextInt(i);
    }

    @Redirect(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextFloat()F"))
    private static float optimizedSpawnEntitiesInChunkFloat(Random instance) {
        return FastRandom.fastRandom.nextFloat();
    }

    @Redirect(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private static int optimizedSpawnEntitiesInChunkInt(Random instance, int i) {
        return FastRandom.fastRandom.nextInt(i);
    }

    @Redirect(method = "pickRandomSpawnEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextFloat()F"))
    private static float optimizedPickRandomSpawnEntry(Random instance) {
        return FastRandom.fastRandom.nextFloat();
    }

    @Redirect(method = "populateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextFloat()F"))
    private static float optimizedPopulateEntitiesFloat(Random instance) {
        return FastRandom.fastRandom.nextFloat();
    }

    @Redirect(method = "populateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private static int optimizedPopulateEntitiesInt(Random instance, int i) {
        return FastRandom.fastRandom.nextInt(i);
    }
}
