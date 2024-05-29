package com.github.tatercertified.potatoptimize.mixin.random.generators;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@IfModAbsent("faster-random")
@Mixin(GaussianGenerator.class)
public class GaussianGeneratorMixin {
    @Final @Shadow public final Random baseRandom = new ThreadLocalRandomImpl();

    @Shadow private boolean hasNextGaussian;

    @Shadow private double nextNextGaussian;

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
