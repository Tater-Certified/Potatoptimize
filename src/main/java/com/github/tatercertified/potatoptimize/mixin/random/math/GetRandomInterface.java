package com.github.tatercertified.potatoptimize.mixin.random.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MathHelper.class)
public interface GetRandomInterface {
    @Accessor("RANDOM")
    public static Random getRandom() {
        throw new AssertionError();
    }
}
