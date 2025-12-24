/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.fabric;

import com.github.tatercertified.vanilla.Potatoptimize;

import net.fabricmc.api.ModInitializer;

public class PotatoptimizeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Potatoptimize.onInitialize();
    }
}
