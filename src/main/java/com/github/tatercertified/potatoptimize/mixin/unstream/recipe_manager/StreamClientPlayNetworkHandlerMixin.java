package com.github.tatercertified.potatoptimize.mixin.unstream.recipe_manager;

import com.github.tatercertified.potatoptimize.utils.interfaces.StreamlessRecipeManagerInterface;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class StreamClientPlayNetworkHandlerMixin {
    @Shadow @Final private RecipeManager recipeManager;

    @Redirect(method = "onSynchronizeRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/recipebook/ClientRecipeBook;reload(Ljava/lang/Iterable;Lnet/minecraft/registry/DynamicRegistryManager;)V"))
    private void redirectStream(ClientRecipeBook instance, Iterable<RecipeEntry<?>> recipes, DynamicRegistryManager registryManager) {
        instance.reload(((StreamlessRecipeManagerInterface)this.recipeManager).values(), ((ClientNetworkAccessor)this).getClient().world.getRegistryManager());
    }
}
