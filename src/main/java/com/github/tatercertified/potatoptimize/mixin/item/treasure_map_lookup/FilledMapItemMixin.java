package com.github.tatercertified.potatoptimize.mixin.item.treasure_map_lookup;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.FilledMapItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Credit to PaperMC patch #0406
 */
@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {

    @Redirect(method = "fillExplorationMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/registry/entry/RegistryEntry;"))
    private static RegistryEntry getBiomeRedirect(ServerWorld instance, BlockPos pos, @Local(ordinal = 0) int l, @Local(ordinal = 0) int o, @Local(ordinal = 0) int i, @Local(ordinal = 0) int m, @Local(ordinal = 0) int n) {
        return instance.getGeneratorStoredBiome((l + o) * i, 0, (m + n) * i);
    }
}
