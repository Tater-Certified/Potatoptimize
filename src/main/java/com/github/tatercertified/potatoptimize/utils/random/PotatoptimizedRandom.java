package com.github.tatercertified.potatoptimize.utils.random;

import net.minecraft.util.math.random.Random;

import java.util.UUID;

public interface PotatoptimizedRandom extends Random {
    double nextDouble(double bound);
    double nextDouble(double origin, double bound);
    float nextFloat(float bound);
    float nextFloat(float origin, float bound);
    float nextGaussian(float mean, float deviation);
    UUID nextUUID();
}
