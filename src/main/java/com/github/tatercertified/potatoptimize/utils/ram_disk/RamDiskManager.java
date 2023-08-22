package com.github.tatercertified.potatoptimize.utils.ram_disk;

import java.io.File;

public interface RamDiskManager {
    void createRamDisk(int size, String mountPath);
    void removeRamDisk();
    void uploadFile(File file);
    void removeFile(File file);
    int getRamDiskSize();
    File[] getRamDiskFiles();
    void adjustRamDiskSize(int newSize);
}
