package com.github.tatercertified.potatoptimize;

import com.github.tatercertified.potatoptimize.config.PotatoptimizeConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.*;

public class Potatoptimize implements ModInitializer {
    public static PotatoptimizeConfig CONFIG;
    public static MinecraftServer almightyServerInstance;
    public static final ExecutorService clientTickExecutor = Executors.newSingleThreadExecutor();
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> almightyServerInstance = server);
    }
}
