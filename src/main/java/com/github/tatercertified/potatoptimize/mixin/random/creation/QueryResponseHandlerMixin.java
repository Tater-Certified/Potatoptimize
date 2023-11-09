package com.github.tatercertified.potatoptimize.mixin.random.creation;

import com.github.tatercertified.potatoptimize.mixin.random.math.GetRandomInterface;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Credit to Mirai patch #0015
@Mixin(QueryResponseHandler.Query.class)
public class QueryResponseHandlerMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;create()Lnet/minecraft/util/math/random/Random;"))
    private Random redirectRandomCreation() {
        return GetRandomInterface.getRandom();
    }
}
