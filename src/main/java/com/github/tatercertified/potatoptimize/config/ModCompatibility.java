package com.github.tatercertified.potatoptimize.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ModCompatibility {
    private static final Logger logger = LogManager.getLogger("PotatoptimizeConfig");
    private static final List<String> disabledMixins = new ArrayList<>();
    // Lithium
    // Disabled in fabric.mod.json

    // Sodium
    // Completely Compatible

    // FerriteCore
    // Completely Compatible

    // Starlight
    // Completely Compatible

    // Krypton
    // Forced Compatibility
    private static final boolean kryptonLoaded = FabricLoader.getInstance().isModLoaded("krypton");
    private static final String[] krypton = {"mixin.logic.var_int"};

    // C2me
    // Completely Compatible

    // ModernFix
    // Forced Compatibility
    private static final boolean modernFixLoaded = FabricLoader.getInstance().isModLoaded("modernfix");
    private static final String[] modernFix = {"mixin.logic.worker_thread"};

    // VMP
    // Completely Compatible

    // ServerCore
    // Forced Compatibility
    private static final boolean serverCoreLoaded = FabricLoader.getInstance().isModLoaded("servercore");
    private static final String[] serverCore = {"mixin.item.map_chunk_loading", "mixin.unstream.pathfinding"};

    public static boolean testFor(String mixin) {
        return disabledMixins.contains(mixin);
    }

    public static void prepareMixins() {
        if (kryptonLoaded) {
            logger.info("Krypton has been detected; Disabling: " + Arrays.toString(krypton));
            disabledMixins.addAll(Arrays.asList(krypton));
        }
        if (modernFixLoaded) {
            logger.info("ModernFix has been detected; Disabling: " + Arrays.toString(modernFix));
            disabledMixins.addAll(Arrays.asList(modernFix));
        }
        if (serverCoreLoaded) {
            logger.info("ServerCore has been detected; Disabling: " + Arrays.toString(serverCore));
            disabledMixins.addAll(Arrays.asList(serverCore));
        }
    }

}
