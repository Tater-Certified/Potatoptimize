package com.github.tatercertified.potatoptimize.mixin.fastmath.rounding;

import com.github.tatercertified.potatoptimize.utils.math.FasterMathUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class FastRoundingServerPlayerMixin {
    @Redirect(
            method = "increaseTravelMotionStats(DDD)V",
            require = 0,
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(D)J"))
    private long fasterRound(double value) {
        return FasterMathUtil.round(value);
    }
}
