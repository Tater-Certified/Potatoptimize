package com.github.tatercertified.potatoptimize.mixin.unstream.recipe_manager;

import com.github.tatercertified.potatoptimize.utils.interfaces.StreamlessRecipeManagerInterface;
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


    @Shadow private Map<Identifier, RecipeEntry<?>> recipesById;

    @Shadow protected abstract <C extends Inventory, T extends Recipe<C>> Collection<RecipeEntry<T>> getAllOfType(RecipeType<T> type);

    /**
     * @author QPCrummer
     * @reason Remove Stream API
     */
    @Overwrite
    public <C extends Inventory, T extends Recipe<C>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, C inventory, World world) {
        for(RecipeEntry<T> recipe : this.getAllOfType(type)) {
            if (recipe.value().matches(inventory, world)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;Lnet/minecraft/util/Identifier;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getAllOfType(Lnet/minecraft/recipe/RecipeType;)Ljava/util/Collection;", shift = At.Shift.BEFORE), cancellable = true)
    private <C extends Inventory, T extends Recipe<C>> void injectToRemoveStream(RecipeType<T> type, C inventory, World world, Identifier id, CallbackInfoReturnable<Optional<Pair<Identifier, RecipeEntry<T>>>> cir) {
        for (RecipeEntry<T> entry : this.getAllOfType(type)) {
            if (entry.value().matches(inventory, world)) {
                cir.setReturnValue(Optional.of(Pair.of(entry.id(), entry)));
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
        for(RecipeEntry<T> recipe : this.getAllOfType(type)) {
            if (recipe.value().matches(inventory, world)) {
                matches.add(recipe);
            }
        }
        matches.sort(Comparator.comparing(recipe -> recipe.value().getResult(world.getRegistryManager()).getTranslationKey()));
        return matches;
    }

    @Override
    public List<RecipeEntry<?>> values() {
        return new ArrayList<>(this.recipesById.values());
    }

    @Override
    public List<Identifier> keys() {
        return new ArrayList<>(this.recipesById.keySet());
    }
}
