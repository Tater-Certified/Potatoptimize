package com.github.tatercertified.potatoptimize.mixin.item.treasure_map_lookup;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.item.FilledMapItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Credit to PaperMC patch #0406
 */
@IfMinecraftVersion(maxVersion = "1.19.2")
@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {

    @Redirect(method = "fillExplorationMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/registry/RegistryEntry;"))
    private static RegistryEntry getBiomeRedirect(ServerWorld instance, BlockPos pos, @Local(ordinal = 0) int l, @Local(ordinal = 0) int o, @Local(ordinal = 0) int i, @Local(ordinal = 0) int m, @Local(ordinal = 0) int n) {
        return instance.getGeneratorStoredBiome((l + o) * i, 0, (m + n) * i);
    }
}
