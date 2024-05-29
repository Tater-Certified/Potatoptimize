package com.github.tatercertified.potatoptimize.mixin.random.generators;

import com.github.tatercertified.potatoptimize.Potatoptimize;
import com.github.tatercertified.potatoptimize.utils.random.SplittableRandomImpl;
import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("faster-random")
@Mixin(ChunkRandom.class)
public class ChunkRandomMixin {
    @Shadow @Final @Mutable private Random baseRandom;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeRandom(Random baseRandom, CallbackInfo ci) {
        // Use TLR if the seed is not cared about as it is better for a multithreaded environment
        if (!Potatoptimize.isUnsafeRandomEnabled) {
            this.baseRandom = new ThreadLocalRandomImpl();
        } else {
            this.baseRandom = new SplittableRandomImpl();
        }
    }
}
