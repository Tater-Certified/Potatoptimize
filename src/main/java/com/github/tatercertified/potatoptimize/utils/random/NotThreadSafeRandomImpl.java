package com.github.tatercertified.potatoptimize.utils.random;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.util.UUID;

// TODO Finish this

/**
 * Credit to PaperMC patch #06911
 */
public class NotThreadSafeRandomImpl implements PotatoptimizedRandom {
    private final long multiplier = 0x5DEECE66DL;
    private final long addend = 0xBL;
    private final long mask = (1L << 48) - 1;
    private long seed;

    public NotThreadSafeRandomImpl(long seed) {
        this.seed = seed;
    }

    public NotThreadSafeRandomImpl() {
        this.setSeed(nextLong());
    }

    @Override
    public double nextDouble(double bound) {
        return 0;
    }

    @Override
    public double nextDouble(double origin, double bound) {
        return 0;
    }

    @Override
    public float nextFloat(float bound) {
        return 0;
    }

    @Override
    public float nextFloat(float origin, float bound) {
        return 0;
    }

    @Override
    public float nextGaussian(float mean, float deviation) {
        return 0;
    }

    @Override
    public UUID nextUUID() {
        return null;
    }

    @Override
    public Random split() {
        return null;
    }

    @Override
    public RandomSplitter nextSplitter() {
        return null;
    }

    @Override
    public void setSeed(long seed) {
        this.seed = scrambleSeed(seed);
    }

    @Override
    public int nextInt() {
        return 0;
    }

    @Override
    public int nextInt(int bound) {
        return fastBoundedLong(this.next(32) & 0xFFFFFFFFL, bound);
    }

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public boolean nextBoolean() {
        return false;
    }

    @Override
    public float nextFloat() {
        return 0;
    }

    @Override
    public double nextDouble() {
        return 0;
    }

    @Override
    public double nextGaussian() {
        return 0;
    }

    private long scrambleSeed(long seed) {
        return (seed ^ multiplier) & mask;
    }

    private int fastBoundedLong(final long randomInteger, final long limit) {
        return (int)((randomInteger * limit) >>> 32);
    }

    private int next(int bits) {
        return (int) (((this.seed = this.seed * multiplier + addend) & mask) >>> (48 - bits));
    }
}
