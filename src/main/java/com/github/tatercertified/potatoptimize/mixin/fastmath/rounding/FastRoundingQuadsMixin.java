package com.github.tatercertified.potatoptimize.mixin.fastmath.rounding;

import com.github.tatercertified.potatoptimize.utils.math.FasterMathUtil;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.client.render.model.BakedQuadFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfModAbsent(value = "vulkanmod")
@Mixin(BakedQuadFactory.class)
public class FastRoundingQuadsMixin {
    @Redirect(
            method = "uvLock",
            require = 0,
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(D)J"))
    private static long fasterRound(double value) {
        return FasterMathUtil.round(value);
    }
}
