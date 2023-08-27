package com.github.tatercertified.potatoptimize.mixin.threading.client_tick;

import com.github.tatercertified.potatoptimize.Potatoptimize;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class ClientTickMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;processDelayedMessages()V"))
    private void redirectProcessDelayedMessages(MessageHandler instance) {
        Potatoptimize.clientTickExecutor.submit(instance::processDelayedMessages);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;tick()V"))
    private void redirectInteractionManager(ClientPlayerInteractionManager instance) {
        Potatoptimize.clientTickExecutor.submit(instance::tick);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;tick()V"))
    private void redirectMusicTracker(MusicTracker instance) {
        Potatoptimize.clientTickExecutor.submit(instance::tick);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;tick(Z)V"))
    private void redirectSoundManager(SoundManager instance, boolean paused) {
        Potatoptimize.clientTickExecutor.submit(() -> instance.tick(paused));
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;tick()V"))
    private void redirectTutorialManager(TutorialManager instance) {
        Potatoptimize.clientTickExecutor.submit(() -> instance.tick());
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void redirectTickWorld(ClientWorld instance, BooleanSupplier shouldKeepTicking) {
        Potatoptimize.clientTickExecutor.submit(() -> instance.tick(shouldKeepTicking));
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;tick()V"))
    private void redirectClientConnection(ClientConnection instance) {
        Potatoptimize.clientTickExecutor.submit(instance::tick);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;tick()V"))
    private void redirectTextureManager(TextureManager instance) {
        Potatoptimize.clientTickExecutor.submit(instance::tick);
    }
}
