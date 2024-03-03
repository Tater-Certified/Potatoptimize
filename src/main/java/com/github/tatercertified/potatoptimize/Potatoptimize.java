package com.github.tatercertified.potatoptimize;

import com.github.tatercertified.potatoptimize.config.PotatoptimizeConfig;
import math.fast.SpeedyMath;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Potatoptimize implements ModInitializer {
    public static PotatoptimizeConfig CONFIG;
    public static MinecraftServer almightyServerInstance;
    public static final ExecutorService clientTickExecutor = Executors.newSingleThreadExecutor();
    public static boolean isUnsafeRandomEnabled;
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }

        // Generate Sin/Cos tables
        // SpeedyMath.cos(90);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> almightyServerInstance = server);
    }
}
