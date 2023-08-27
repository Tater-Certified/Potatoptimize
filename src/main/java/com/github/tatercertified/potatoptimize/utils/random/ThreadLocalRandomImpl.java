package com.github.tatercertified.potatoptimize.utils.random;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomImpl implements PotatoptimizedRandom {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();;
    public ThreadLocalRandomImpl() {
    }

    public ThreadLocalRandomImpl(long ignoredSeed) {
    }

    @Override
    public Random split() {
        return this;
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new Splitter(0L,this);
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public int nextInt() {
        return this.random.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return this.random.nextInt(bound);
    }

    @Override
    public int nextBetween(int min, int max) {
        if (min >= max) {
            return min;
        }
        return this.random.nextInt(min, max);
    }

    @Override
    public long nextLong() {
        return this.random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return this.random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return this.random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return this.random.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return quickGaussian();
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

    @Override
    public double nextDouble(double bound) {
        return this.random.nextDouble(bound);
    }

    @Override
    public double nextDouble(double min, double max) {
        if (min >= max) {
            return min;
        }
        return this.random.nextDouble(min, max);
    }

    @Override
    public float nextFloat(float bound) {
        return this.random.nextFloat(bound);
    }

    @Override
    public float nextFloat(float min, float max) {
        if (min >= max) {
            return min;
        }
        return this.random.nextFloat(min, max);
    }

    @Override
    public float nextGaussian(float mean, float deviation) {
        return (float) (mean + quickGaussian() * deviation);
    }

    @Override
    public UUID nextUUID() {
        return new UUID( nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L, nextLong()  & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE);
    }

    public record Splitter(long seed, ThreadLocalRandomImpl instance) implements RandomSplitter {
        @Override
        public Random split(String seed) {
            int i = seed.hashCode();
            return new ThreadLocalRandomImpl((long)i ^ this.seed);
        }

        @Override
        public Random split(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new ThreadLocalRandomImpl(m);
        }

        @Override
        public void addDebugInfo(StringBuilder info) {
            info.append("seed: ").append(this.seed);
        }
    }
}
