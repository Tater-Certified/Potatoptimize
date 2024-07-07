package com.github.tatercertified.potatoptimize.mixin.random.world;

import com.github.tatercertified.potatoptimize.utils.random.ThreadLocalRandomImpl;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfModAbsent(value = "faster-random", aliases = {"c2me-fixes-worldgen-threading-issues"})
@Mixin(value = World.class)
public class RandomWorldMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt()I"))
    private int redirectRandomNextInt(Random instance) {
        return ThreadLocalRandomImpl.INSTANCE.nextInt();
    }
}
