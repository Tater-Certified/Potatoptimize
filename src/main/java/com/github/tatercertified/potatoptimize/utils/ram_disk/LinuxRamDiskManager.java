package com.github.tatercertified.potatoptimize.utils.ram_disk;

import java.io.File;

public class LinuxRamDiskManager implements RamDiskManager{
    private int size;
    private String mountPath;
    @Override
    public void createRamDisk(int size, String mountPath) {

    }

    @Override
    public void removeRamDisk() {

    }

    @Override
    public void uploadFile(File file) {

    }

    @Override
    public void removeFile(File file) {

    }

    @Override
    public int getRamDiskSize() {
        return 0;
    }

    @Override
    public File[] getRamDiskFiles() {
        return new File[0];
    }

    @Override
    public void adjustRamDiskSize(int newSize) {

    }
}
