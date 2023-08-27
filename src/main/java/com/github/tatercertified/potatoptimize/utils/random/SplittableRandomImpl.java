package com.github.tatercertified.potatoptimize.utils.random;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.util.SplittableRandom;
import java.util.UUID;

public class SplittableRandomImpl implements PotatoptimizedRandom {
    private SplittableRandom random = new SplittableRandom();;
    private long seed;

    public SplittableRandomImpl() {
    }

    public SplittableRandomImpl(long seed) {
        setSeed(seed);
    }

    @Override
    public double nextDouble(double bound) {
        return this.random.nextDouble(bound);
    }

    @Override
    public double nextDouble(double origin, double bound) {
        return this.random.nextDouble(origin, bound);
    }

    @Override
    public float nextFloat(float bound) {
        return this.random.nextFloat(bound);
    }

    @Override
    public float nextFloat(float origin, float bound) {
        return this.random.nextFloat(origin, bound);
    }

    @Override
    public float nextGaussian(float mean, float deviation) {
        return (float) (mean + quickGaussian() * deviation);
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
    public UUID nextUUID() {
        return new UUID( nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L, nextLong()  & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE);
    }

    @Override
    public Random split() {
        return new SplittableRandomImpl(nextLong());
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new Splitter(this.seed, this);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
        this.random = new SplittableRandom(this.seed);
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

    private record Splitter(long seed, SplittableRandomImpl instance) implements RandomSplitter {
        @Override
        public Random split(String seed) {
            int i = seed.hashCode();
            return new SplittableRandomImpl((long)i ^ this.seed);
        }

        @Override
        public Random split(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new SplittableRandomImpl(m);
        }

        @Override
        public void addDebugInfo(StringBuilder info) {
            info.append("seed: ").append(this.seed);
        }
    }
}
