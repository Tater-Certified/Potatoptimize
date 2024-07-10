package com.github.tatercertified.potatoptimize.mixin.random.generators;

import com.github.tatercertified.potatoptimize.utils.random.XorShiftRandomImpl;
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

@IfModAbsent(value = "faster-random")
@Mixin(ChunkRandom.class)
public class ChunkRandomMixin {
    @Shadow @Final @Mutable private Random baseRandom;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeRandom(Random baseRandom, CallbackInfo ci) {
        this.baseRandom = new XorShiftRandomImpl();
    }
}
