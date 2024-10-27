package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.utils.async.AsyncIOUtil;
import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkManagerInterface;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.updater.WorldUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Credit to PaperMC PR #10171
@IfModAbsent(value = "c2me")
@Mixin(WorldUpdater.class)
public class WorldUpdaterMixin {
    @Redirect(method = "updateWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/PersistentStateManager;save()V"))
    private void redirectSave(PersistentStateManager instance) {
        try {
            ((AsyncChunkManagerInterface) instance).close();
        } catch (Exception e) {
            AsyncIOUtil.LOGGER.error("Failed to close Persistent State Manager: ", e);
        }
    }
}
