package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.registry.RegistryWrapper;

import java.io.File;

public interface AsyncChunkSaveInterface {
    void save(File file, boolean async, RegistryWrapper.WrapperLookup registryLookup);
}
