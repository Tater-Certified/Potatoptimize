/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.vanilla.entity.navigation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This optimization prevents mobs that are in a vehicle from trying to pathfind. As far as I can
 * tell, entities still pathfind when in boats in vanilla, however their movements aren't completed.
 * This optimization should have vanilla parity
 *
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(PathNavigation.class)
public class CancelNavigationMixin {
    @Shadow @Final protected Mob mob;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void cancelTick(CallbackInfo ci) {
        if (mob.isPassenger()) {
            ci.cancel();
        }
    }
}
