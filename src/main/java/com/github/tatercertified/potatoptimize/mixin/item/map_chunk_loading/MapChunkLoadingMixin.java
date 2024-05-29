package com.github.tatercertified.potatoptimize.mixin.item.map_chunk_loading;

import com.github.tatercertified.potatoptimize.utils.interfaces.ChunkQuery;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfMinecraftVersion(maxVersion = "1.19.2")
@IfModAbsent("servercore")
@Mixin(FilledMapItem.class)
public class MapChunkLoadingMixin {

    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getWorldChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/WorldChunk;"))
    private WorldChunk lazyUpdateColors(World instance, BlockPos pos) {
        return ((ChunkQuery)(instance)).getChunkIfLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;isEmpty()Z"))
    private boolean additionalContext(WorldChunk instance) {
        return instance != null && !instance.isEmpty();
    }

}
