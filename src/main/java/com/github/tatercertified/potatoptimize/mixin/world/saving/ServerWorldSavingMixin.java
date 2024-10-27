package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkManagerInterface;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Credit to PaperMC PR #10171
@IfModAbsent(value = "c2me")
@Mixin(ServerWorld.class)
public abstract class ServerWorldSavingMixin {

    @Shadow @Nullable private EnderDragonFight enderDragonFight;

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract ServerChunkManager getChunkManager();

    private void saveLevelAsync() {
        if (this.enderDragonFight != null) {
            this.server.getSaveProperties().setDragonFight(this.enderDragonFight.toData());
        }

        // TODO Fix Async Chunk Saving
        ((AsyncChunkManagerInterface)this.getChunkManager().getPersistentStateManager()).saveAsync();
    }

    @Redirect(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;save(Z)V"))
    private void redirectToAsync(ServerChunkManager instance, boolean flush) {
        if (flush) {
            instance.save(true);
        } else {
            this.saveLevelAsync();
        }
    }
}
