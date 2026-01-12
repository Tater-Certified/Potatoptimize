/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config.mixintree;

import com.github.tatercertified.vanilla.config.PotatoptimizeConfig;
import com.mojang.datafixers.util.Pair;

import java.util.List;

public class MixinTree {
    MixinTreeNode root = new MixinTreeNode(); // Mixin package
    private int overrides;

    /**
     * Builds a config tree
     *
     * @param overrides Mixin overrides from the default config, user config, and external mods
     */
    public MixinTree(List<Pair<String[], Pair<String, Boolean>>> overrides) {
        // Build the tree
        for (Pair<String[], Pair<String, Boolean>> override : overrides) {
            root.addChild(override, 1, this);
        }
    }

    public void incrementOverrides() {
        this.overrides++;
    }

    public int getOverrides() {
        return this.overrides;
    }

    /**
     * Determines if a Mixin should be enabled
     *
     * @return NodeData if there is an override and MISSING_NODE NodeData if there isn't an override
     */
    public NodeData isEnabled(String mixin) {
        return this.root.isEnabled(PotatoptimizeConfig.slice(mixin), 1);
    }
}
