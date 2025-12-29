/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.neuralnexus.taterapi.meta.*;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PotatoptimizeConfig {
    private static final Logger LOGGER = LogManager.getLogger("PotatoptimizeConfig");

    private static final String JSON_KEY_MOD_OPTIONS = "potatoptimize:options";

    private final Map<String, Option> options = new HashMap<>();
    private final Set<Option> optionsWithDependencies = new ObjectLinkedOpenHashSet<>();

    private PotatoptimizeConfig() {
        // Defines the default rules which can be configured by the user or other mods.
        try (InputStream defaultPropertiesStream =
                PotatoptimizeConfig.class.getResourceAsStream(
                        "/assets/potatoptimize/potatoptimize-mixin-config-default.properties")) {
            if (defaultPropertiesStream == null) {
                throw new IllegalStateException(
                        "Potatoptimize mixin config default properties could not be read!");
            }
            try (BufferedReader propertiesReader =
                    new BufferedReader(new InputStreamReader(defaultPropertiesStream))) {
                Properties properties = new Properties();
                properties.load(propertiesReader);
                properties.forEach(
                        (ruleName, enabled) ->
                                this.addMixinRule(
                                        (String) ruleName, Boolean.parseBoolean((String) enabled)));
            } catch (IOException e) {
                LOGGER.error("Potatoptimize mixin config default properties could not be read!", e);
                throw new IllegalStateException(
                        "Potatoptimize mixin config default properties could not be read!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the configuration file from the specified location. If it does not exist, a new
     * configuration file will be created. The file on disk will then be updated to include any new
     * options.
     */
    public static PotatoptimizeConfig load(File file) {
        PotatoptimizeConfig config = new PotatoptimizeConfig();

        if (file.exists()) {
            Properties props = new Properties();

            try (FileInputStream fin = new FileInputStream(file)) {
                props.load(fin);
            } catch (IOException e) {
                throw new RuntimeException("Could not load config file", e);
            }

            config.readProperties(props);
        } else {
            try {
                writeDefaultConfig(file);
            } catch (IOException e) {
                LOGGER.warn("Could not write default configuration file", e);
            }
        }

        config.applyModOverrides();

        // Check dependencies several times, because one iteration may disable a rule required by
        // another rule
        // This terminates because each additional iteration will disable one or more rules, and
        // there is only a finite number of rules
        //noinspection StatementWithEmptyBody
        while (config.applyDependencies()) {
            //noinspection UnnecessarySemicolon
            ;
        }

        return config;
    }

    /**
     * Defines a dependency between two registered mixin rules. If a dependency is not satisfied,
     * the mixin will be disabled.
     *
     * @param rule the mixin rule that requires another rule to be set to a given value
     * @param dependency the mixin rule the given rule depends on
     * @param requiredValue the required value of the dependency
     */
    @SuppressWarnings("SameParameterValue")
    private void addRuleDependency(String rule, String dependency, boolean requiredValue) {
        Option option = this.options.get(rule);
        if (option == null) {
            LOGGER.error(
                    "Option {} for dependency '{} depends on {}={}' not found. Skipping.",
                    rule,
                    rule,
                    dependency,
                    requiredValue);
            return;
        }
        Option dependencyOption = this.options.get(dependency);
        if (dependencyOption == null) {
            LOGGER.error(
                    "Option {} for dependency '{} depends on {}={}' not found. Skipping.",
                    dependency,
                    rule,
                    dependency,
                    requiredValue);
            return;
        }
        option.addDependency(dependencyOption, requiredValue);
        this.optionsWithDependencies.add(option);
    }

    /**
     * Defines a Mixin rule which can be configured by users and other mods.
     *
     * @param mixin The name of the mixin package which will be controlled by this rule
     * @param enabled True if the rule will be enabled by default, otherwise false
     * @throws IllegalStateException If a rule with that name already exists
     */
    private void addMixinRule(String mixin, boolean enabled) {
        if (this.options.putIfAbsent(mixin, new Option(mixin, enabled, false)) != null) {
            throw new IllegalStateException("Mixin rule already defined: " + mixin);
        }
    }

    private void readProperties(Properties props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            Option option = this.options.get(key);

            if (option == null) {
                LOGGER.warn("No configuration key exists with name '{}', ignoring", key);
                continue;
            }

            boolean enabled;

            if (value.equalsIgnoreCase("true")) {
                enabled = true;
            } else if (value.equalsIgnoreCase("false")) {
                enabled = false;
            } else {
                LOGGER.warn(
                        "Invalid value '{}' encountered for configuration key '{}', ignoring",
                        value,
                        key);
                continue;
            }

            option.setEnabled(enabled, true);
        }
    }

    private void applyModOverrides() {
        for (ModContainer<?> container : MetaAPI.instance().mods(MetaAPI.instance().platform())) {
            Map<String, Boolean> overrides;
            if (container.platform().isFabric()) {
                overrides = getOverridesFabric();
            } else if (container.platform().isForge()) {
                overrides = getOverridesForge();
            } else if (container.platform().isNeoForge()) {
                overrides = getOverridesNeoForge(container);
            } else {
                overrides = getOverridesSponge();
            }

            for (Map.Entry<String, Boolean> override : overrides.entrySet()) {
                applyModOverride(container.info(), override.getKey(), override.getValue());
            }
        }
    }

    private Map<String, Boolean> getOverridesFabric() {
        try (JarFile file = new JarFile(this.getJarFile())) {
            ZipEntry entry = file.getEntry("fabric.mod.json");
            try (final InputStream is = file.getInputStream(entry);
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject custom = root.getAsJsonObject("custom");
                if (custom == null) {
                    return Map.of();
                } else {
                    JsonObject potatoptimizeOptions = custom.getAsJsonObject(JSON_KEY_MOD_OPTIONS);
                    Type mapType = new TypeToken<@NotNull Map<String, Boolean>>() {}.getType();
                    return new Gson().fromJson(potatoptimizeOptions, mapType);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getJarFile() {
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            try {
                return new File(location.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException(
                    "Cannot determine code source for class: " + this.getClass().getName());
        }
    }

    private Map<String, Boolean> getOverridesSponge() {
        try (JarFile file = new JarFile(this.getJarFile())) {
            ZipEntry entry = file.getEntry("META-INF/sponge_plugins.json");
            try (final InputStream is = file.getInputStream(entry);
                 final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject custom = root.getAsJsonObject("custom");
                if (custom == null) {
                    return Map.of();
                } else {
                    JsonObject potatoptimizeOptions = custom.getAsJsonObject(JSON_KEY_MOD_OPTIONS);
                    Type mapType = new TypeToken<@NotNull Map<String, Boolean>>() {}.getType();
                    return new Gson().fromJson(potatoptimizeOptions, mapType);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Boolean> getOverridesForge() {
        try (JarFile file = new JarFile(this.getJarFile())) {
            ZipEntry zipEntry = file.getEntry("META-INF/mods.toml");
            try (final InputStream is = file.getInputStream(zipEntry)) {
                TomlParseResult result = Toml.parse(is);
                TomlArray customArray = result.getArray("custom");
                if (customArray != null) {
                    for (int i = 0; i < customArray.size(); i++) {
                        Object element = customArray.get(i);
                        if (element instanceof TomlParseResult table) {
                            Map<String, Boolean> map = new HashMap<>();
                            for (Map.Entry<String, Object> entry : table.entrySet()) {
                                map.put(entry.getKey(), (Boolean) entry.getValue());
                            }
                            return map;
                        }
                    }
                }
                return Map.of();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Boolean> getOverridesNeoForge(ModContainer<?> container) {
        try (JarFile file = new JarFile(this.getJarFile())) {
            ZipEntry zipEntry = file.getEntry("META-INF/neoforge.mods.toml");
            try (final InputStream is = file.getInputStream(zipEntry)) {
                TomlParseResult result = Toml.parse(is);
                TomlArray customArray = result.getArray("custom");
                if (customArray != null) {
                    for (int i = 0; i < customArray.size(); i++) {
                        Object element = customArray.get(i);
                        if (element instanceof TomlParseResult table) {
                            Map<String, Boolean> map = new HashMap<>();
                            for (Map.Entry<String, Object> entry : table.entrySet()) {
                                map.put(entry.getKey(), (Boolean) entry.getValue());
                            }
                            return map;
                        }
                    }
                }
                return Map.of();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void applyModOverride(ModInfo meta, String name, Boolean value) {
        if (!name.startsWith("mixin.")) {
            name = getMixinRuleName(name);
        }
        Option option = this.options.get(name);

        if (option == null) {
            LOGGER.warn(
                    "Mod '{}' attempted to override option '{}', which doesn't exist, ignoring",
                    meta.id(),
                    name);
            return;
        }

        if (value == null) {
            LOGGER.warn(
                    "Mod '{}' attempted to override option '{}' with an invalid value, ignoring",
                    meta.id(),
                    name);
            return;
        }

        // disabling the option takes precedence over enabling
        if (!value && option.isEnabled()) {
            option.clearModsDefiningValue();
        }

        if (!value || option.isEnabled() || option.getDefiningMods().isEmpty()) {
            option.addModOverride(value, meta.id());
        }
    }

    /**
     * Returns the effective option for the specified class name. This traverses the package path of
     * the given mixin and checks each root for configuration rules. If a configuration rule
     * disables a package, all mixins located in that package and its children will be disabled. The
     * effective option is that of the highest-priority rule, either a enable rule at the end of the
     * chain or a disable rule at the earliest point in the chain.
     *
     * @return Null if no options matched the given mixin name, otherwise the effective option for
     *     this Mixin
     */
    public Option getEffectiveOptionForMixin(String mixinClassName) {
        int lastSplit = 0;
        int nextSplit;

        Option rule = null;

        while ((nextSplit = mixinClassName.indexOf('.', lastSplit)) != -1) {
            String key = getMixinRuleName(mixinClassName.substring(0, nextSplit));

            Option candidate = this.options.get(key);

            if (candidate != null) {
                rule = candidate;

                if (!rule.isEnabled()) {
                    return rule;
                }
            }

            lastSplit = nextSplit + 1;
        }

        return rule;
    }

    /** Tests all dependencies and disables options when their dependencies are not met. */
    private boolean applyDependencies() {
        boolean changed = false;
        for (Option optionWithDependency : this.optionsWithDependencies) {
            changed |= optionWithDependency.disableIfDependenciesNotMet(LOGGER, this);
        }
        return changed;
    }

    private static void writeDefaultConfig(File file) throws IOException {
        File dir = file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Could not create parent directories");
            }
        } else if (!dir.isDirectory()) {
            throw new IOException("The parent file is not a directory");
        }

        try (Writer writer = new FileWriter(file)) {
            writer.write("# This is the configuration file for Potatoptimize.\n");
            writer.write(
                    "# This file exists for enabling and disabling optimizations via mixins.\n");
            writer.write(
                    "# Before configuring anything, take a backup of the worlds that will be opened.\n");
            writer.write("#\n");
            writer.write(
                    "# You can find information on editing this file and all the available options here:\n");
            writer.write(
                    "# https://github.com/Tater-Certified/Potatoptimize/wiki/Configuration-File\n");
            writer.write("#\n");
            writer.write("# By default, this file will be empty except for this notice.\n");
        }
    }

    private static String getMixinRuleName(String name) {
        return "mixin." + name;
    }

    public int getOptionCount() {
        return this.options.size();
    }

    public int getOptionOverrideCount() {
        return (int) this.options.values().stream().filter(Option::isOverridden).count();
    }

    public Option getParent(Option option) {
        String optionName = option.getName();
        int split;

        if ((split = optionName.lastIndexOf('.')) != -1) {
            String key = optionName.substring(0, split);
            return this.options.get(key);
        }
        return null;
    }
}
