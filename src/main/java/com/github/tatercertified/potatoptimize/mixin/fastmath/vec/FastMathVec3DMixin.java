package com.github.tatercertified.potatoptimize.mixin.fastmath.vec;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.*;

@Mixin(Vec3d.class)
public class FastMathVec3DMixin {
    @Mutable
    @Shadow
    @Final
    public double x;

    @Mutable
    @Shadow @Final public double y;

    @Mutable
    @Shadow @Final public double z;
    private Vec3d cachedNormalized;

    /**
     * @author QPCrummer
     * @reason Cache normalized Vec
     */
    @Overwrite
    public Vec3d normalize() {
        if (cachedNormalized == null) {
            double squaredLength = x * x + y * y + z * z;
            if (squaredLength < 1.0E-8) {
                cachedNormalized = Vec3d.ZERO;
            } else {
                double invLength = 1.0 / Math.sqrt(squaredLength);
                cachedNormalized = new Vec3d(x * invLength, y * invLength, z * invLength);
            }
        }
        return cachedNormalized;
    }
}
