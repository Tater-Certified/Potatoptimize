package com.github.tatercertified.potatoptimize.mixin.threading.entity_ticking;

import com.github.tatercertified.potatoptimize.utils.interfaces.ServerEntityThreadInterface;
import com.github.tatercertified.potatoptimize.utils.threading.ThreadedTaskExecutor;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.ServerTask;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class EntityServerThreadingMixin extends ReentrantThreadExecutor<ServerTask> implements ServerEntityThreadInterface {

    @Unique
    private ThreadedTaskExecutor executor;

    public EntityServerThreadingMixin(final String string) {
        super(string);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void createThreads(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.executor = new ThreadedTaskExecutor(this, 4);
    }

    @Override
    public ThreadedTaskExecutor getEntityExecutor() {
        return this.executor;
    }
}
