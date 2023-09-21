package com.github.tatercertified.potatoptimize.mixin.threading.entity_ticking;

import com.github.tatercertified.potatoptimize.interfaces.ServerEntityThreadInterface;
import com.github.tatercertified.potatoptimize.utils.threading.ThreadedTaskExecutor;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class EntityServerThreadingMixin implements ServerEntityThreadInterface {

    @Unique
    private ThreadedTaskExecutor executor;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void createThreads(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.executor = new ThreadedTaskExecutor(4);
    }

    @Override
    public ThreadedTaskExecutor getEntityExecutor() {
        return this.executor;
    }
}
