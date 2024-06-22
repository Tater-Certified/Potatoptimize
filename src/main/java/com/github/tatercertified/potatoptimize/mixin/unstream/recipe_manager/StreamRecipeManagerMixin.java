package com.github.tatercertified.potatoptimize.mixin.unstream.recipe_manager;

import com.github.tatercertified.potatoptimize.utils.interfaces.StreamlessRecipeManagerInterface;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
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

    @Shadow protected abstract <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeEntry<T>> getAllOfType(RecipeType<T> type);

    /**
     * @author QPCrummer
     * @reason Remove Stream API
     */
    @Overwrite
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, I input, World world) {
        for(RecipeEntry<T> recipe : this.getAllOfType(type)) {
            if (recipe.value().matches(input, world)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/input/RecipeInput;Lnet/minecraft/world/World;Lnet/minecraft/recipe/RecipeEntry;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getAllOfType(Lnet/minecraft/recipe/RecipeType;)Ljava/util/Collection;", shift = At.Shift.BEFORE), cancellable = true)
    private <I extends RecipeInput, T extends Recipe<I>> void injectToRemoveStream(RecipeType<T> type, I input, World world, RecipeEntry<T> recipe, CallbackInfoReturnable<Optional<RecipeEntry<T>>> cir) {
        cir.setReturnValue(getFirstMatch(type, input, world));
    }

    /**
     * @author QPCrummer
     * @reason Remove StreamAPI
     */
    @Overwrite
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeEntry<T>> getAllMatches(RecipeType<T> type, I input, World world) {
        List<RecipeEntry<T>> matches = new ArrayList<>();
        for(RecipeEntry<T> recipe : this.getAllOfType(type)) {
            if (recipe.value().matches(input, world)) {
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
