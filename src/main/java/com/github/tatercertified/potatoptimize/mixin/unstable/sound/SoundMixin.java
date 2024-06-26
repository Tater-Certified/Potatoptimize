package com.github.tatercertified.potatoptimize.mixin.unstable.sound;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlaySoundS2CPacket.class)
public class SoundMixin {
    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    private void overWrite(RegistryByteBuf buf, CallbackInfo ci) {
        ci.cancel();
    }
}
