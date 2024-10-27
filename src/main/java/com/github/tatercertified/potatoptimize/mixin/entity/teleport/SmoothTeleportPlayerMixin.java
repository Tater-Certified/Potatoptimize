package com.github.tatercertified.potatoptimize.mixin.entity.teleport;

import com.github.tatercertified.potatoptimize.utils.interfaces.SmoothTeleportInterface;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class SmoothTeleportPlayerMixin implements SmoothTeleportInterface {
    //@Shadow public abstract void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch);

    @Shadow public abstract boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera);

    private boolean smoothTeleport;

    @Override
    public void setSmoothTeleport(boolean bool) {
        this.smoothTeleport = bool;
    }

    @Override
    public boolean shouldSmoothTeleport() {
        return this.smoothTeleport;
    }

    // TODO Find good places to use this
    @Override
    public void smoothTeleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
        this.setSmoothTeleport(true);
        this.teleport(targetWorld, x, y, z, null, yaw, pitch, true); // TODO Can I make position flags null?
        this.setSmoothTeleport(false);
    }
}
