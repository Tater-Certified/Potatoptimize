package com.github.tatercertified.potatoptimize.mixin.memory.map_dedupe;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

// Credit to Mirai patch #0031
@Mixin(Advancement.class)
public class AdvancementsMixin {

    @Redirect(method = "<init>(Ljava/util/Optional;Ljava/util/Optional;Lnet/minecraft/advancement/AdvancementRewards;Ljava/util/Map;Lnet/minecraft/advancement/AdvancementRequirements;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;copyOf(Ljava/util/Map;)Ljava/util/Map;"))
    private static Map<String, AdvancementCriterion<?>> removeCopyOfMap(Map<String, AdvancementCriterion<?>> map, @Local(ordinal = 0, argsOnly = true) Map<String, AdvancementCriterion<?>> criteria) {
        return criteria;
    }
}
