/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config.mixintree;

import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class MixinTreeNode {
    private NodeData data;
    private final Map<String, MixinTreeNode> children = new HashMap<>();

    public void setEnabled(boolean enabled, String mod) {
        this.data = new NodeData(mod, enabled);
    }

    public void addChild(Pair<String[], Pair<String, Boolean>> sliced, int index, MixinTree ref) {
        if (this.data != null && !this.data.enabled()) {
            // No need to waste time traversing on already disabled Mixins
            return;
        }
        String[] packageParts = sliced.getFirst();
        String packageName = packageParts[index];
        MixinTreeNode child = new MixinTreeNode();
        if (children.containsKey(packageName)) {
            child = children.get(packageName);
        } else {
            children.put(packageName, child);
        }

        if (index == packageParts.length - 1) {
            child.setEnabled(sliced.getSecond().getSecond(), sliced.getSecond().getFirst());
            ref.incrementOverrides();
            return;
        }
        child.addChild(sliced, index + 1, ref);
    }

    /**
     * Determines if a Mixin should be enabled
     *
     * @param packageParts The mixin split at the decimals
     * @param index The current index of recursion (depth)
     * @return NodeData if there is an override and MISSING_NODE NodeData if there isn't an override
     */
    public NodeData isEnabled(String[] packageParts, int index) {
        if (index == packageParts.length - 1) {
            // Arrived at node
            return this.data;
        }

        if (this.data == null || this.data.enabled()) {
            String currentPart = packageParts[index];
            MixinTreeNode nextNode = children.get(currentPart);
            if (nextNode == null) {
                // If the node doesn't exist, then it is assumed on
                return new NodeData(null, true);
            } else {
                return nextNode.isEnabled(packageParts, index + 1);
            }
        }
        return this.data;
    }
}
