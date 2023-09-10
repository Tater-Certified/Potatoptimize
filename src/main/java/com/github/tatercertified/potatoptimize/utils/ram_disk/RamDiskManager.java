package com.github.tatercertified.potatoptimize.utils.ram_disk;

import net.minecraft.world.storage.RegionFile;

import java.io.File;

public interface RamDiskManager {
    void createRamDisk(int size, String mountPath);
    void removeRamDisk();
    void uploadFile(RegionFile file);
    void removeFile(RegionFile file);
    int getRamDiskSize();
    RegionFile[] getRamDiskFiles();
    void adjustRamDiskSize(int newSize);
}
