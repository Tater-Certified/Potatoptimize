package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkManagerInterface;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public abstract class ServerWorldSavingMixin {

    @Shadow @Nullable private EnderDragonFight enderDragonFight;

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract ServerChunkManager getChunkManager();

    @Unique
    private void saveLevel(boolean async) {
        if (this.enderDragonFight != null) {
            this.server.getSaveProperties().setDragonFight(this.enderDragonFight.toData());
        }

        ((AsyncChunkManagerInterface)this.getChunkManager().getPersistentStateManager()).save(async);
    }

    @Redirect(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;saveLevel()V"))
    private void redirectToAsync(ServerWorld instance) {
        //TODO Figure out what "close" is in
        this.saveLevel(true);
    }
}
