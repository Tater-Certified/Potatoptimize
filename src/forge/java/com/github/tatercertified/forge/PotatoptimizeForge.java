/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/NoDim/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.forge;

import com.github.tatercertified.vanilla.Potatoptimize;
import net.minecraftforge.fml.common.Mod;

@Mod(Potatoptimize.MOD_ID)
public class PotatoptimizeForge {
    public PotatoptimizeForge() {
        Potatoptimize.onInitialize();
    }
}
