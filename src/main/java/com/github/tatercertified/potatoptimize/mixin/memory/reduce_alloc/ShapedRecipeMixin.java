package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.ArrayConstants;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {

    @ModifyReturnValue(method = "removePadding", at = @At(value = "RETURN", ordinal = 0))
    private static String[] redirectReturn(String[] original) {
        return ArrayConstants.emptyStringArray;
    }
}
