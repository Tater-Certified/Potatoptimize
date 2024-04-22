package com.github.tatercertified.potatoptimize.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class ModCompatibility {
    private static final Logger logger = LogManager.getLogger("PotatoptimizeConfig");
    private static final List<String> disabledMixins = new ArrayList<>();

    public static boolean testFor(String mixin) {
        return disabledMixins.contains(mixin);
    }

    public static void prepareMixins() {
        addModCompatibility("krypton", "Krypton", new String[] {"mixin.logic.var_int"});
        addModCompatibility("modernfix", "ModernFix", new String[] {"mixin.logic.worker_thread"});
        addModCompatibility("servercore", "ServerCore", new String[] {"mixin.item.map_chunk_loading", "mixin.unstream.pathfinding"});
        addModCompatibility("chronos-carpet-addons", "ChronosCarpetAddons", new String[] {"mixin.entity.collisions"});
        addModCompatibility("faster-random", "FasterRandom", new String[] {"mixin.random.entity", "mixin.random.math", "mixin.random.world"});
        addModCompatibility("c2me", "C2me", new String[] {"mixin.world.saving"});
        addModCompatibility("lithium", "Lithium", new String[] {"mixin.world.explosion", "mixin.entity.villager_task"});
    }

    private static void addModCompatibility(String modId, String visualName, String[] mixins) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            logger.info(visualName + " has been detected; Disabling: " + Arrays.toString(mixins));
            disabledMixins.addAll(Arrays.asList(mixins));
        }
    }

}
