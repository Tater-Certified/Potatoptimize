package com.github.tatercertified.potatoptimize.mixin.item.map_chunk_loading;

import com.github.tatercertified.potatoptimize.utils.interfaces.ChunkQuery;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfModAbsent(value = "servercore", aliases = {"c2me"})
@Mixin(FilledMapItem.class)
public class MapChunkLoadingMixin {

    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/WorldChunk;"))
    private WorldChunk lazyUpdateColors(World instance, int i, int j) {
        return ((ChunkQuery)(instance)).getChunkIfLoaded(ChunkSectionPos.getSectionCoord(i), ChunkSectionPos.getSectionCoord(j));
    }

    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;isEmpty()Z"))
    private boolean additionalContext(WorldChunk instance) {
        return instance != null && !instance.isEmpty();
    }

}
