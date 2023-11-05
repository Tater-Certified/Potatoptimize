package com.github.tatercertified.potatoptimize.mixin.fastmath.rounding;

import com.github.tatercertified.potatoptimize.utils.math.FasterMathUtil;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SurfaceBuilder.class)
public class FastRoundingSurfaceBuildMixin {
    @Redirect(
            method = "getTerracottaBlock(III)Lnet/minecraft/block/BlockState;",
            require = 0,
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(D)J"))
    private long fasterRound(double value) {
        return FasterMathUtil.round(value);
    }
}
