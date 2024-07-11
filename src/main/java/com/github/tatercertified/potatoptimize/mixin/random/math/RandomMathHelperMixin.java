package com.github.tatercertified.potatoptimize.mixin.random.math;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent(value = "faster-random")
@Mixin(MathHelper.class)
public abstract class RandomMathHelperMixin {

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
}
