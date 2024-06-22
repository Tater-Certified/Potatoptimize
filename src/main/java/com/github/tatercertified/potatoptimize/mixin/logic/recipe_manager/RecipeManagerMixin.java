package com.github.tatercertified.potatoptimize.mixin.logic.recipe_manager;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {


    @Shadow protected abstract <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeEntry<T>> getAllOfType(RecipeType<T> type);

    /**
     * @author QPCrummer & Leaf Patch #0023
     * @reason Optimize RecipeManager List Creation
     */
    @Overwrite
    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeEntry<T>> listAllOfType(RecipeType<T> type) {
        return new ArrayList<>(this.getAllOfType(type));
    }
}
