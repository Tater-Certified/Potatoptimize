package com.github.tatercertified.potatoptimize.utils.ram_disk;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.world.storage.RegionFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class LinuxRamDiskManager implements RamDiskManager{
    private int size;
    private String mountPath;
    private CacheLoader<int[], RegionFile> cacheLoader;
    private LoadingCache<int[], RegionFile> cache;

    public LinuxRamDiskManager() {
    }
    @Override
    public void createRamDisk(int size, String mountPath) {

        if (this.cache != null) {
            removeRamDisk();
        }

        this.cacheLoader = new CacheLoader<>() {
            @Override
            public @NotNull RegionFile load(int @NotNull [] key) {
                return null;
            }
        };
        this.cache = CacheBuilder.newBuilder().build(this.cacheLoader);
    }

    @Override
    public void removeRamDisk() {
        this.cache.invalidateAll();
        this.cache = null;
    }

    @Override
    public void uploadFile(RegionFile file) {
        this.cache.put(null, file);
    }

    @Override
    public void removeFile(RegionFile file) {
        this.cache.invalidate(null);
    }

    @Override
    public int getRamDiskSize() {
        return this.size;
    }

    @Override
    public RegionFile[] getRamDiskFiles() {
        return this.cache.asMap().values().toArray(new RegionFile[0]);
    }

    @Override
    public void adjustRamDiskSize(int newSize) {
    }
}
