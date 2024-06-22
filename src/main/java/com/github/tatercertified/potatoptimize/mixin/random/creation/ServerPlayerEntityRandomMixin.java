package com.github.tatercertified.potatoptimize.mixin.random.creation;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityRandomMixin {

    @Shadow public abstract ServerWorld getServerWorld();

    @Redirect(method = "getWorldSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;create()Lnet/minecraft/util/math/random/Random;"))
    private Random redirectCreatedRandom() {
        return getServerWorld().random;
    }


}
