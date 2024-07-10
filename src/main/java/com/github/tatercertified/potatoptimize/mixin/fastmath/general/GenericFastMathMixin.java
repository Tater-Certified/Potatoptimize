package com.github.tatercertified.potatoptimize.mixin.fastmath.general;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.stream.IntStream;

@Mixin(MathHelper.class)
public class GenericFastMathMixin {

    /**
     * @author QPCrummer
     * @reason Slightly more optimized
     */
    @Overwrite
    public static boolean method_34945(Vec3d origin, Vec3d direction, Box box) {

        double d = (box.minX + box.maxX) * 0.5;
        double e = (box.maxX - box.minX) * 0.5;
        double f = origin.x - d;
        if (Math.abs(f) > e && f * direction.x > 0.0) {
            return false;
        }

        double g = (box.minY + box.maxY) * 0.5;
        double h = (box.maxY - box.minY) * 0.5;
        double i = origin.y - g;
        if (Math.abs(i) > h && i * direction.y >= 0.0) {
            return false;
        }

        double j = (box.minZ + box.maxZ) * 0.5;
        double k = (box.maxZ - box.minZ) * 0.5;
        double l = origin.z - j;
        if (Math.abs(l) > k && l * direction.z >= 0.0) {
            return false;
        }

        double m = Math.abs(direction.x);
        double n = Math.abs(direction.y);
        double o = Math.abs(direction.z);
        double p = direction.y * l - direction.z * i;
        if (Math.abs(p) > h * o + k * n || Math.abs(direction.z * f - direction.x * l) > e * o + k * m) {
            return false;
        }

        return Math.abs(direction.x * i - direction.y * f) < e * n + h * m;
    }

    /**
     * @author QPCrummer
     * @reason Slightly more optimized
     */
    @Overwrite
    public static IntStream stream(int seed, int lowerBound, int upperBound, int steps) {
        if (lowerBound > upperBound || steps < 1) {
            throw new IllegalArgumentException("Invalid bounds or steps");
        }

        int start = Math.max(seed - Math.abs(seed - lowerBound), lowerBound);
        int end = Math.min(seed + Math.abs(seed - upperBound), upperBound);

        return IntStream.iterate(start, i -> i != end, i -> i + (i < seed ? steps : -steps))
                .limit((end - start) / steps + 1);
    }
}
