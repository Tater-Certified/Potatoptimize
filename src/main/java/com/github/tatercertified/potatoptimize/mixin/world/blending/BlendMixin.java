package com.github.tatercertified.potatoptimize.mixin.world.blending;

import com.github.tatercertified.potatoptimize.utils.math.FasterMathUtil;
import net.minecraft.world.gen.chunk.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Credit Gale patch #0019
@Mixin(Blender.class)
public class BlendMixin {
    @Redirect(method = "getBlendOffset", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floorMod(DD)D"))
    private static double redirectBlendOffset(double dividend, double divisor) {
        return FasterMathUtil.positiveModuloForPositiveIntegerDivisor(dividend, divisor);
    }
}
