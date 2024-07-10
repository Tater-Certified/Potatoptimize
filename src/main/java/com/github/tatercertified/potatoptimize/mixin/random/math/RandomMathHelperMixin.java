package com.github.tatercertified.potatoptimize.mixin.random.math;

import com.github.tatercertified.potatoptimize.utils.random.PotatoptimizedRandom;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@IfModAbsent(value = "faster-random")
@Mixin(MathHelper.class)
public abstract class RandomMathHelperMixin {

    @Shadow
    public static float nextBetween(Random random, float min, float max) {
        return 0;
    }

    @ModifyReturnValue(method = "nextGaussian", at = @At("RETURN"))
    private static float optimizedGaussian(float original, @Local(ordinal = 0, argsOnly = true) float mean, @Local(ordinal = 0, argsOnly = true) float deviation, @Local(ordinal = 0, argsOnly = true) Random random) {
        return (float) (mean + quickGaussian(random) * deviation);
    }

    // Gaussian Code
    private static double quickGaussian(Random random) {
        long randomBits = random.nextLong();
        long evenChunks = randomBits & EVEN_CHUNKS;
        long oddChunks = (randomBits & ODD_CHUNKS) >>> 5;
        long sum = chunkSum(evenChunks + oddChunks) - 186;
        return sum / 31.0;
    }

    private static long chunkSum(long bits) {
        long sum = bits + (bits >>> 40);
        sum += sum >>> 20;
        sum += sum >>> 10;
        sum &= (1<<10)-1;
        return sum;
    }

    private static final long EVEN_CHUNKS = 0x7c1f07c1f07c1fL;
    private static final long ODD_CHUNKS  = EVEN_CHUNKS << 5;

    @ModifyReturnValue(method = "nextFloat", at = @At("RETURN"))
    private static float optimizedNextBetween(float original, @Local(ordinal = 0, argsOnly = true) Random random, @Local(ordinal = 0, argsOnly = true) float min, @Local(ordinal = 0, argsOnly = true) float max) {
        if (random instanceof PotatoptimizedRandom potatoptimizedRandom) {
            return potatoptimizedRandom.nextFloat(min, max);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "randomUuid(Lnet/minecraft/util/math/random/Random;)Ljava/util/UUID;", at = @At("RETURN"))
    private static UUID optimizedRandomUUID(UUID original, @Local(ordinal = 0, argsOnly = true) Random random) {
        return new UUID(random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L, random.nextLong()  & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE);
    }

    @ModifyReturnValue(method = "nextInt", at = @At("RETURN"))
    private static int optimizedNextInt(int original, @Local(ordinal = 0, argsOnly = true) Random random, @Local(ordinal = 0, argsOnly = true) int min, @Local(ordinal = 0, argsOnly = true) int max) {
        return random.nextBetween(min, max);
    }

    @ModifyReturnValue(method = "nextFloat", at = @At("RETURN"))
    private static float optimizedNextFloat(float original, @Local(ordinal = 0, argsOnly = true) Random random, @Local(ordinal = 0, argsOnly = true) float min, @Local(ordinal = 0, argsOnly = true) float max) {
        return nextBetween(random, min, max);
    }

    @ModifyReturnValue(method = "nextDouble", at = @At("RETURN"))
    private static double optimizedNextDouble(double original, @Local(ordinal = 0, argsOnly = true) Random random, @Local(ordinal = 0, argsOnly = true) double min, @Local(ordinal = 0, argsOnly = true) double max) {
        if (random instanceof PotatoptimizedRandom potatoptimizedRandom) {
            return potatoptimizedRandom.nextDouble(min, max);
        } else {
            return  original;
        }
    }
}
