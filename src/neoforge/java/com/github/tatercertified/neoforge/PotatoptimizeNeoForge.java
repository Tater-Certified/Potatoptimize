/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.neoforge;

import com.github.tatercertified.vanilla.Potatoptimize;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Potatoptimize.MOD_ID)
public class PotatoptimizeNeoForge {
    public PotatoptimizeNeoForge(IEventBus eventBus) {
        Potatoptimize.onInitialize();
    }
}
