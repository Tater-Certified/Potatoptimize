/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.semivanilla.entity.navigation;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * This optimization prevents mobs that are in a vehicle from trying to pathfind. As far as I can
 * tell, entities still pathfind when in boats in vanilla, however their movements aren't completed.
 * This optimization should have vanilla parity
 *
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.1.0
 */
@Mixin(PathNavigation.class)
public class CancelNavigationMixin {
    @Shadow @Final protected Mob mob;

    @WrapMethod(method = "tick")
    private void potatoptimize$cancelPathFinding(Operation<Void> original) {
        // Skip ticking mob AI if the original mob is riding in a nonliving vehicle
        if (!mob.isPassenger() || mob.getVehicle() instanceof LivingEntity) {
            original.call();
        }
    }
}
