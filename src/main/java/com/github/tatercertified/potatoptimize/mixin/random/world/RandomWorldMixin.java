package com.github.tatercertified.potatoptimize.mixin.random.world;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.github.tatercertified.potatoptimize.utils.random.XorShiftRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfModAbsent(value = "faster-random", aliases = {"c2me-fixes-worldgen-threading-issues"})
@Mixin(value = World.class)
public class RandomWorldMixin {
    @Shadow @Final public Random random;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;create()Lnet/minecraft/util/math/random/Random;"))
    private Random redirectRandomCreation() {
        return new XorShiftRandomImpl();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;createThreadSafe()Lnet/minecraft/util/math/random/Random;"))
    private Random redirectThreadSafeRandomCreation() {
        return new ThreadLocalRandomImpl();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt()I"))
    private int redirectRandomNextInt(Random instance) {
        return this.random.nextInt();
    }
}
