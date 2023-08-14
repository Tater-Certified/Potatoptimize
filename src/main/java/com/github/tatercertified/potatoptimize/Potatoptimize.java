package com.github.tatercertified.potatoptimize;

import com.github.tatercertified.potatoptimize.config.PotatoptimizeConfig;
import net.fabricmc.api.ModInitializer;

public class Potatoptimize implements ModInitializer {
    public static PotatoptimizeConfig CONFIG;
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }
    }
}
