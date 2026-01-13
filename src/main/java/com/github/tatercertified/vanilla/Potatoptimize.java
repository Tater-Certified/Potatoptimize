/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla;

public class Potatoptimize {
    public static boolean configLoaded;
    public static final String MOD_ID = "potatoptimize";

    public static void onInitialize() {
        if (!configLoaded) {
            throw new IllegalStateException(
                    "The mixin plugin did not initialize the config! Did it not load?");
        }
    }
}
