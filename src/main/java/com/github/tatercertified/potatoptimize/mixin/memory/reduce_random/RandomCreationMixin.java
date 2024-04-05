package com.github.tatercertified.potatoptimize.mixin.memory.reduce_random;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Random.class)
public interface RandomCreationMixin {

    @Redirect(method = "create()Lnet/minecraft/util/math/random/Random;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;create(J)Lnet/minecraft/util/math/random/Random;"))
    private static Random redirectNewRandoms(long seed) {
        return ThreadLocalRandomImpl.INSTANCE;
    }
}
