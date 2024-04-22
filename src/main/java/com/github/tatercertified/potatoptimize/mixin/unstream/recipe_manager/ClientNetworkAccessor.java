package com.github.tatercertified.potatoptimize.mixin.unstream.recipe_manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientCommonNetworkHandler.class)
public interface ClientNetworkAccessor {
    @Accessor
    MinecraftClient getClient();
}
