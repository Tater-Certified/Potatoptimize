/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config;

import com.github.tatercertified.vanilla.Potatoptimize;
import com.moulberry.mixinconstraints.MixinConstraints;
import com.moulberry.mixinconstraints.mixin.MixinConstraintsBootstrap;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.enums.Platform;
import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

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
    private final Logger logger = LogManager.getLogger("PotatoptimizeConfig");
    private PotatoptimizeConfig config;

    @Override
    public void onLoad(String mixinPackage) {
        // Loader detection
        detectLoader();

        if (MetaAPI.instance().platform().isFabric()) {
            MIXIN_PACKAGE_ROOT = "com.github.tatercertified.y_intmdry.mixin.";
        } else {
            MIXIN_PACKAGE_ROOT = "com.github.tatercertified.vanilla.mixin.";
        }

        MixinConstraintsBootstrap.init(mixinPackage);

        try {
            this.config = PotatoptimizeConfig.load(new File("./config/potatoptimize.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Could not load configuration file for Potatoptimize", e);
        }

        this.logger.info(
                "Loaded configuration file for Potatoptimize: {} options available, {} override(s) found",
                this.config.getOptionCount(),
                this.config.getOptionOverrideCount());

        Potatoptimize.CONFIG = this.config;
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
        Option option = this.config.getEffectiveOptionForMixin(mixin);

        if (option == null) {
            this.logger.error(
                    "No rules matched mixin '{}', treating as foreign and disabling!", mixin);

            return false;
        }

        if (option.isOverridden()) {
            String source = "[unknown]";

            if (option.isUserDefined()) {
                source = "user configuration";
            } else if (option.isModDefined()) {
                source = "mods [" + String.join(", ", option.getDefiningMods()) + "]";
            }

            if (option.isEnabled()) {
                this.logger.warn(
                        "Force-enabling mixin '{}' as rule '{}' (added by {}) enables it",
                        mixin,
                        option.getName(),
                        source);
            } else {
                this.logger.warn(
                        "Force-disabling mixin '{}' as rule '{}' (added by {}) disables it and children",
                        mixin,
                        option.getName(),
                        source);
            }
        }

        // Mod compatibility override
        if (!MixinConstraints.shouldApplyMixin(targetClassName, mixinClassName)) {
            return false;
        }

        return option.isEnabled();
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

    private void detectLoader() {
        for (Platform platform :
                Set.of(Platform.FABRIC, Platform.SPONGE, Platform.FORGE, Platform.NEOFORGE)) {
            if (Constraint.builder().platform(platform.ref()).build().result()) {
                switch (platform) {
                    case FABRIC -> TaterMetadata.initFabric();
                    case FORGE -> TaterMetadata.initForge();
                    case NEOFORGE -> TaterMetadata.initNeoForge();
                    default -> TaterMetadata.initSponge();
                }
            }
        }
    }
}
