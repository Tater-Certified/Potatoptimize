/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.config;

import com.github.tatercertified.vanilla.config.mixintree.MixinTree;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;

import dev.neuralnexus.taterapi.meta.*;

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
    private static final String INTERNAL = "Potatoptimize";
    private static final String USER = "User";

    private static final List<Pair<String[], Pair<String, Boolean>>> mixinOverrides =
            new ArrayList<>();
    public MixinTree tree;

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
                                mixinOverrides.add(
                                        new Pair<>(
                                                slice((String) ruleName),
                                                new Pair<>(
                                                        INTERNAL,
                                                        Boolean.parseBoolean((String) enabled)))));
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
        config.applyModOverrides();

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

        // Generate Mixin Tree
        config.tree = new MixinTree(mixinOverrides);
        return config;
    }

    private void readProperties(Properties props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

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

            mixinOverrides.add(new Pair<>(slice(key), new Pair<>(USER, enabled)));
        }
    }

    private void applyModOverrides() {

        for (ModContainer<?> container : MetaAPI.instance().mods(MixinConfig.platform.ref())) {
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
                if (override.getValue() == null) {
                    LOGGER.warn(
                            "Mod '{}' attempted to override option '{}' with an invalid value, ignoring",
                            container.id(),
                            override.getKey());
                    continue;
                }

                mixinOverrides.add(
                        new Pair<>(
                                slice(override.getKey()),
                                new Pair<>(container.name(), override.getValue())));
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
                // handling nested JAR URI
                String path = location.toString();
                if (path.startsWith("jar:")) {
                    // extract the actual file path from jar:file:/path!/entry
                    path = path.substring(4); // Remove "jar:"
                    int bangIndex = path.indexOf('!');
                    if (bangIndex != -1) {
                        path = path.substring(0, bangIndex);
                    }
                    return new File(new java.net.URI(path));
                }
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

    public static String[] slice(String string) {
        return string.split("\\.");
    }
}
