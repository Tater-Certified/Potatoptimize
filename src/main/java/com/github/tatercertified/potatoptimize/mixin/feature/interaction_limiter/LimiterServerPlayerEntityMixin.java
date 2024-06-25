package com.github.tatercertified.potatoptimize.mixin.feature.interaction_limiter;

import com.github.tatercertified.potatoptimize.utils.interfaces.InteractionLimiterInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(ServerPlayerEntity.class)
public class LimiterServerPlayerEntityMixin implements InteractionLimiterInterface {
    private int instaBreakCountPerTick = 0;
    private int placeBlockCountPerTick = 0;

    private void resetOperationCountPerTick() {
        instaBreakCountPerTick = 0;
        placeBlockCountPerTick = 0;
    }

    public int getInstaBreakCountPerTick() {
        return instaBreakCountPerTick;
    }

    public int getPlaceBlockCountPerTick() {
        return placeBlockCountPerTick;
    }

    public void addInstaBreakCountPerTick() {
        ++instaBreakCountPerTick;
    }

    public void addPlaceBlockCountPerTick() {
        ++placeBlockCountPerTick;
    }

    public boolean allowOperation() {
        return (instaBreakCountPerTick == 0 || placeBlockCountPerTick == 0) && (instaBreakCountPerTick <= 1 && placeBlockCountPerTick <= 2);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SculkShriekerWarningManager;tick()V"))
    private void injectResetOperationCounts(CallbackInfo ci) {
        this.resetOperationCountPerTick();
    }
}
