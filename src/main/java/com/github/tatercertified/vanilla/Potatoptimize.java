package com.github.tatercertified.vanilla;

import com.github.tatercertified.vanilla.config.PotatoptimizeConfig;

public class Potatoptimize {
    public static PotatoptimizeConfig CONFIG;
    public static final String MOD_ID = "potatoptimize";

    public static void onInitialize() {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }
    }
}
