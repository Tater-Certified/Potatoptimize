package com.github.tatercertified.potatoptimize.mixin.random.math;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@IfModAbsent(value = "faster-random")
@Mixin(MathHelper.class)
public class RandomMathHelperMixin {

    //TODO Determine the impact of having RANDOM Shadowed and not final
    @Unique
    private static Random RANDOM = new ThreadLocalRandomImpl();

    @Inject(method = "nextGaussian", at = @At("HEAD"), cancellable = true)
    private static void optimizedGaussian(Random random, float mean, float deviation, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(((ThreadLocalRandomImpl)RANDOM).nextGaussian(mean, deviation));
    }

    @Inject(method = "nextBetween(Lnet/minecraft/util/math/random/Random;II)I", at = @At("HEAD"), cancellable = true)
    private static void optimizedNextBetween(Random random, int min, int max, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RANDOM.nextBetween(min, max));
    }

    @Inject(method = "nextBetween(Lnet/minecraft/util/math/random/Random;FF)F", at = @At("HEAD"), cancellable = true)
    private static void optimizedNextBetween(Random random, float min, float max, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(((ThreadLocalRandomImpl)RANDOM).nextFloat(min, max));
    }

    @Inject(method = "randomUuid(Lnet/minecraft/util/math/random/Random;)Ljava/util/UUID;", at = @At("HEAD"), cancellable = true)
    private static void optimizedRandomUUID(Random random, CallbackInfoReturnable<UUID> cir) {
        cir.setReturnValue(((ThreadLocalRandomImpl)RANDOM).nextUUID());
    }

    @Inject(method = "nextInt", at = @At("HEAD"), cancellable = true)
    private static void optimizedNextInt(Random random, int min, int max, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RANDOM.nextBetween(min, max));
    }

    @Inject(method = "nextFloat", at = @At("HEAD"), cancellable = true)
    private static void optimizedNextFloat(Random random, float min, float max, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(((ThreadLocalRandomImpl)RANDOM).nextFloat(min, max));
    }

    @Inject(method = "nextDouble", at = @At("HEAD"), cancellable = true)
    private static void optimizedNextDouble(Random random, double min, double max, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(((ThreadLocalRandomImpl)RANDOM).nextDouble(min, max));
    }

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;createThreadSafe()Lnet/minecraft/util/math/random/Random;", shift = At.Shift.AFTER))
    private static void reassignRandom(CallbackInfo ci) {
        RANDOM = new ThreadLocalRandomImpl();
    }
}
