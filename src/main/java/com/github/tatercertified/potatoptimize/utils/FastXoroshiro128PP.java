package com.github.tatercertified.potatoptimize.utils;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.util.UUID;

public class FastXoroshiro128PP implements Random {

    private long state0;
    private long state1;
    private long seed;

    public FastXoroshiro128PP(long seed) {
        setSeed(seed);
    }
    private long splitMix64(long x) {
        x = (x ^ (x >>> 30)) * 0xBF58476D1CE4E5B9L;
        x = (x ^ (x >>> 27)) * 0x94D049BB133111EBL;
        return x ^ (x >>> 31);
    }

    @Override
    public Random split() {
        return new FastXoroshiro128PP(this.seed);
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new Splitter(this.seed);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
        long combinedSeed = seed ^ 0x9E3779B97F4A7C15L;
        state0 = splitMix64(combinedSeed);
        state1 = splitMix64(state0);
    }

    @Override
    public int nextInt() {
        long t = state0 + state1;
        state1 ^= state0;
        state0 = Long.rotateLeft(state0, 55) ^ state1 ^ (state1 << 14);
        state1 = Long.rotateLeft(state1, 36);
        return (int) (t >>> 32);
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) {
            return 0;
        }

        int r = nextInt();
        int m = bound - 1;
        if ((bound & m) == 0) {
            return r & m;
        }
        return Math.abs(r % bound);
    }

    @Override
    public int nextBetween(int min, int max) {
        if (min >= max) {
            return min;
        }
        return min + nextInt(max - min + 1);
    }

    @Override
    public long nextLong() {
        long high = ((long) nextInt()) << 32;
        long low = nextInt() & 0xFFFFFFFFL;
        return high | low;
    }

    @Override
    public boolean nextBoolean() {
        return (nextInt(2) != 0);
    }

    @Override
    public float nextFloat() {
        return (nextLong() & Long.MAX_VALUE) / (float) Long.MAX_VALUE;
    }

    @Override
    public double nextDouble() {
        return (nextLong() & Long.MAX_VALUE) / (double) Long.MAX_VALUE;
    }

    @Override
    public double nextGaussian() {
        return quickGaussian();
    }

    @Override
    public int nextBetweenExclusive(int min, int max) {
        if (min >= max - 1) {
            return min;
        }
        return min + 1 + nextInt(max - min - 1);
    }

    public float nextFloatBetween(float min, float max) {
        if (min >= max) {
            return min;
        }
        return nextFloat() * (max - min) + min;
    }

    public double nextDoubleBetween(double min, double max) {
        if (min >= max) {
            return min;
        }
        return nextDouble() * (max - min) + min;
    }

    public UUID nextUUID() {
        return new UUID( nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L, nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE);
    }

    // Gaussian Code
    private double quickGaussian() {
        long randomBits = nextLong();
        long evenChunks = randomBits & EVEN_CHUNKS;
        long oddChunks = (randomBits & ODD_CHUNKS) >>> 5;
        long sum = chunkSum(evenChunks + oddChunks) - 186;
        return sum / 31.0;
    }

    private long chunkSum(long bits) {
        long sum = bits + (bits >>> 40);
        sum += sum >>> 20;
        sum += sum >>> 10;
        sum &= (1<<10)-1;
        return sum;
    }

    private final long EVEN_CHUNKS = 0x7c1f07c1f07c1fL;
    private final long ODD_CHUNKS  = EVEN_CHUNKS << 5;


    public static class Splitter
            implements RandomSplitter {
        private final long seed;

        public Splitter(long seed) {
            this.seed = seed;
        }

        @Override
        public Random split(int x, int y, int z) {
            return new FastXoroshiro128PP(seed);
        }

        @Override
        public Random split(String seed) {
            return new FastXoroshiro128PP(Long.getLong(seed));
        }

        @Override
        @VisibleForTesting
        public void addDebugInfo(StringBuilder info) {
        }
    }
}
