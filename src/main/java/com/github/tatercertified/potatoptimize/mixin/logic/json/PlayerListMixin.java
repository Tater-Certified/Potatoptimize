package com.github.tatercertified.potatoptimize.mixin.logic.json;

import net.minecraft.server.dedicated.DedicatedPlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DedicatedPlayerManager.class)
public class PlayerListMixin {
    @Redirect(method = {"addToOperators", "removeFromOperators"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedPlayerManager;saveOpList()V"))
    private void removeExtraSave(DedicatedPlayerManager instance) {
    }
}
