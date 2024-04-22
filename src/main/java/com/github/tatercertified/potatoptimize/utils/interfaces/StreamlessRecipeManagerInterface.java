package com.github.tatercertified.potatoptimize.utils.interfaces;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public interface StreamlessRecipeManagerInterface {
    List<RecipeEntry<?>> values();
    List<Identifier> keys();
}
