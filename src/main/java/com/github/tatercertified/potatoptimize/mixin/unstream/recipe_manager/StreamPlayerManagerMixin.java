package com.github.tatercertified.potatoptimize.mixin.unstream.recipe_manager;

import com.github.tatercertified.potatoptimize.utils.interfaces.StreamlessRecipeManagerInterface;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(PlayerManager.class)
public class StreamPlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 4))
    private void redirectStream(ServerPlayNetworkHandler instance, Packet packet) {
        instance.sendPacket(new SynchronizeRecipesS2CPacket(((StreamlessRecipeManagerInterface)this.server.getRecipeManager()).values()));
    }

    @Redirect(method = "onDataPacksReloaded", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;values()Ljava/util/Collection;"))
    private Collection<RecipeEntry<?>> redirectToRemoveStream(RecipeManager instance) {
        return ((StreamlessRecipeManagerInterface)instance).values();
    }
}
