package com.github.tatercertified.potatoptimize.mixin.networking.block_breaking;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Credit Mirai patch #0061
@Mixin(ServerWorld.class)
public class CacheBlockBreakPacketMixin {
    @Unique
    private BlockBreakingProgressS2CPacket packet;

    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"))
    private void setPacketNull(int entityId, BlockPos pos, int progress, CallbackInfo ci) {
        this.packet = null;
    }

    @Redirect(method = "setBlockBreakingInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void redirectPacketSent(ServerPlayNetworkHandler instance, Packet packetBad, @Local(ordinal = 0) int entityId, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) int progress, @Local(ordinal = 0)ServerPlayerEntity serverPlayerEntity) {
        if (this.packet == null) this.packet = new BlockBreakingProgressS2CPacket(entityId, pos, progress);
        serverPlayerEntity.networkHandler.sendPacket(this.packet);
    }
}
