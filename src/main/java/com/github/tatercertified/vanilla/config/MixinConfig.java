/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config;

import com.github.tatercertified.vanilla.Potatoptimize;
import com.github.tatercertified.vanilla.config.mixintree.NodeData;
import com.moulberry.mixinconstraints.MixinConstraints;
import com.moulberry.mixinconstraints.mixin.MixinConstraintsBootstrap;

import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.util.List;
import java.util.Set;

public class MixinConfig implements IMixinConfigPlugin {
    private static String MIXIN_PACKAGE_ROOT;
    public static Platform platform;
    private final Logger logger = LogManager.getLogger("PotatoptimizeConfig");
    private PotatoptimizeConfig config;

    @Override
    public void onLoad(String mixinPackage) {
        // Loader detection
        platform = Platforms.detectPrimary();

        if (platform.ref().isFabric()) {
            MIXIN_PACKAGE_ROOT = "com.github.tatercertified.y_intmdry.";
        } else {
            MIXIN_PACKAGE_ROOT = "com.github.tatercertified.vanilla.";
        }

        MixinConstraintsBootstrap.init(mixinPackage);

        try {
            this.config = PotatoptimizeConfig.load(new File("./config/potatoptimize.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Could not load configuration file for Potatoptimize", e);
        }

        this.logger.info(
                "Loaded optimizations overrides for Potatoptimize: {} override(s) found",
                this.config.tree.getOverrides());

        Potatoptimize.configLoaded = true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(MIXIN_PACKAGE_ROOT)) {
            this.logger.error(
                    "Expected mixin '{}' to start with package root '{}', treating as foreign and "
                            + "disabling!",
                    mixinClassName,
                    MIXIN_PACKAGE_ROOT);

            return false;
        }

        String mixin = mixinClassName.substring(MIXIN_PACKAGE_ROOT.length());
        NodeData data = this.config.tree.isEnabled(mixin);

        String source;

        if (data.source() == null) {
            // Not overridden
            return MixinConstraints.shouldApplyMixin(targetClassName, mixinClassName);
        } else {
            source = data.source();
        }

        if (data.isUser()) {
            if (data.enabled()) {
                this.logger.warn("Force-enabling mixin '{}' as the user enables it", mixin);
            } else {
                this.logger.warn("Force-disabling mixin '{}' as the user disables it", mixin);
            }
        } else {
            if (data.enabled()) {
                this.logger.warn("Force-enabling mixin '{}' as '{}' enables it", mixin, source);
            } else {
                this.logger.warn("Force-disabling mixin '{}' as '{}' disables it", mixin, source);
            }
        }

        // Mod compatibility override
        if (!MixinConstraints.shouldApplyMixin(targetClassName, mixinClassName)) {
            return false;
        }

        return data.enabled();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo) {}

    @Override
    public void postApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo) {}
}
