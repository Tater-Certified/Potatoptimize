package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

public interface AsyncChunkSaveInterface {
    void saveAsync(Path file, RegistryWrapper.WrapperLookup registryLookup, @Nullable ExecutorService ioExecutor);
}
