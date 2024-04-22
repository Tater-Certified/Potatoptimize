package com.github.tatercertified.potatoptimize.mixin.unstream.recipe_manager;

import com.github.tatercertified.potatoptimize.utils.interfaces.StreamlessRecipeManagerInterface;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(RecipeManager.class)
public abstract class StreamRecipeManagerMixin implements StreamlessRecipeManagerInterface {


    @Shadow protected abstract <C extends Inventory, T extends Recipe<C>> Map<Identifier, RecipeEntry<T>> getAllOfType(RecipeType<T> type);

    @Shadow private Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> recipes;

    /**
     * @author QPCrummer
     * @reason Remove Stream API
     */
    @Overwrite
    public <C extends Inventory, T extends Recipe<C>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, C inventory, World world) {
        for(RecipeEntry<T> recipe : this.getAllOfType(type).values()) {
            if (recipe.value().matches(inventory, world)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;Lnet/minecraft/util/Identifier;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", shift = At.Shift.BEFORE), cancellable = true)
    private <C extends Inventory, T extends Recipe<C>> void injectToRemoveStream(RecipeType<T> type, C inventory, World world, Identifier id, CallbackInfoReturnable<Optional<Pair<Identifier, RecipeEntry<T>>>> cir, @Local(ordinal = 0) Map<Identifier, RecipeEntry<T>> map) {
        for (Map.Entry<Identifier, RecipeEntry<T>> entry : map.entrySet()) {
            if (entry.getValue().value().matches(inventory, world)) {
                Identifier key = entry.getKey();
                RecipeEntry<T> value = entry.getValue();
                cir.setReturnValue(Optional.of(Pair.of(key, value)));
            }
        }

        cir.setReturnValue(Optional.empty());
    }

    /**
     * @author QPCrummer
     * @reason Remove StreamAPI
     */
    @Overwrite
    public <C extends Inventory, T extends Recipe<C>> List<RecipeEntry<T>> getAllMatches(RecipeType<T> type, C inventory, World world) {
        List<RecipeEntry<T>> matches = new ArrayList<>();
        for(RecipeEntry<T> recipe : this.getAllOfType(type).values()) {
            if (recipe.value().matches(inventory, world)) {
                matches.add(recipe);
            }
        }
        matches.sort(Comparator.comparing(recipe -> recipe.value().getResult(world.getRegistryManager()).getTranslationKey()));
        return matches;
    }

    @Override
    public List<RecipeEntry<?>> values() {
        List<RecipeEntry<?>> allRecipes = new ArrayList<>();
        for (Map<Identifier, RecipeEntry<?>> innerMap : this.recipes.values()) {
            allRecipes.addAll(innerMap.values());
        }
        return allRecipes;
    }

    @Override
    public List<Identifier> keys() {
        List<Identifier> allRecipes = new ArrayList<>();
        for (Map<Identifier, RecipeEntry<?>> innerMap : this.recipes.values()) {
            allRecipes.addAll(innerMap.keySet());
        }
        return allRecipes;
    }
}
