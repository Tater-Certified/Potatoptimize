package com.github.tatercertified.potatoptimize.mixin.entity.teleport.portal;

import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This optimization cancels the expensive entity bounding box collision tests for large entities (> 1 block).
 * This optimization does NOT have vanilla parity.
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@IfModAbsent(value = "chronos-carpet-addons")
@Mixin(PortalShape.class)
public class NetherPortalMixin {
    @Inject(method = "findCollisionFreePosition", at = @At("HEAD"), cancellable = true)
    private static void findOpenPos(Vec3 fallback, ServerLevel world, Entity entity, EntityDimensions dimensions, CallbackInfoReturnable<Vec3> cir) {
        cir.setReturnValue(fallback);
    }
}
