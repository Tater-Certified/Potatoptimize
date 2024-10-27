package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.utils.async.AsyncIOUtil;
import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkManagerInterface;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Credit to PaperMC PR #10171
@IfModAbsent(value = "c2me")
@Mixin(ServerChunkManager.class)
public class SavingServerChunkManagerMixin {
    @Shadow @Final private PersistentStateManager persistentStateManager;

    @Inject(method = "save", at = @At("TAIL"))
    private void savePersistentStorage(boolean flush, CallbackInfo ci) {
        try {
            ((AsyncChunkManagerInterface)this.persistentStateManager).saveAsync();
        } catch (Exception e) {
            AsyncIOUtil.LOGGER.error("Failed to save persistent states: ", e);
        }
    }
}
