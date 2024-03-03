package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.server.world.ServerWorld;

public interface SmoothTeleportInterface {
    void setSmoothTeleport(boolean bool);
    boolean shouldSmoothTeleport();
    void smoothTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch);
}
