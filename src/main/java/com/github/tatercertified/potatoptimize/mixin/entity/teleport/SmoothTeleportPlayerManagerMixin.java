package com.github.tatercertified.potatoptimize.mixin.entity.teleport;

import com.github.tatercertified.potatoptimize.utils.interfaces.SmoothTeleportInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public class SmoothTeleportPlayerManagerMixin {
    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    private void removeSendPacket(ServerPlayNetworkHandler instance, Packet packet, @Local(ordinal = 0) ServerPlayerEntity serverPlayerEntity) {
        if (!((SmoothTeleportInterface)serverPlayerEntity).shouldSmoothTeleport()) {
            instance.sendPacket(packet);
        }
    }

    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;requestTeleport(DDDFF)V"))
    private void removeSpawnRequest(ServerPlayNetworkHandler instance, double x, double y, double z, float yaw, float pitch, @Local(ordinal = 0) ServerPlayerEntity serverPlayerEntity) {
        if (!((SmoothTeleportInterface)serverPlayerEntity).shouldSmoothTeleport()) {
            instance.requestTeleport(x, y, z, yaw, pitch);
        }
    }
}
