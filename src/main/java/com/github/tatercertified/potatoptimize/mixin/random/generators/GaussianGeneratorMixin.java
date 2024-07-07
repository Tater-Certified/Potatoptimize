package com.github.tatercertified.potatoptimize.mixin.random.generators;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.github.tatercertified.potatoptimize.utils.random.XorShiftRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfModAbsent(value = "faster-random")
@Mixin(GaussianGenerator.class)
public class GaussianGeneratorMixin {

    @Shadow private boolean hasNextGaussian;

    @Shadow private double nextNextGaussian;

    @Mutable @Shadow @Final public Random baseRandom;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/random/GaussianGenerator;baseRandom:Lnet/minecraft/util/math/random/Random;"))
    private void redirectRandomAssignment(GaussianGenerator instance, Random value) {
        this.baseRandom = new XorShiftRandomImpl();
    }

    /**
     * @author QPCrummer
     * @reason Faster Gaussian Implementation
     */
    @Overwrite
    public double next() {
        if (this.hasNextGaussian) {
            this.hasNextGaussian = false;
            return this.nextNextGaussian;
        }
        this.nextNextGaussian = this.baseRandom.nextGaussian();
        this.hasNextGaussian = true;
        return this.nextNextGaussian;
    }
}
