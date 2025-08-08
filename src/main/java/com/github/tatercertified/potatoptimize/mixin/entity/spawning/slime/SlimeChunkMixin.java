package com.github.tatercertified.potatoptimize.mixin.entity.spawning.slime;

import com.github.tatercertified.potatoptimize.utils.interfaces.SlimeChunkInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class SlimeChunkMixin implements SlimeChunkInterface {

    private boolean slimeChunk;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;)V", at = @At("TAIL"))
    private void assignSlimeChunk1(Level world, ChunkPos pos, CallbackInfo ci) {
        setSlimeChunk(world, pos);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V", at = @At("TAIL"))
    private void assignSlimeChunk2(ServerLevel world, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor entityLoader, CallbackInfo ci) {
        setSlimeChunk(world, protoChunk.getPos());
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("TAIL"))
    private void assignSlimeChunk3(Level world, ChunkPos pos, UpgradeData upgradeData, LevelChunkTicks blockTickScheduler, LevelChunkTicks fluidTickScheduler, long inhabitedTime, LevelChunkSection[] sectionArrayInitializer, LevelChunk.PostLoadProcessor entityLoader, BlendingData blendingData, CallbackInfo ci) {
        setSlimeChunk(world, pos);
    }

    @Override
    public boolean isSlimeChunk() {
        return slimeChunk;
    }

    private void setSlimeChunk(Level world, ChunkPos pos) {
        if (world instanceof WorldGenLevel genLevel) {
            RandomSource random = WorldgenRandom.seedSlimeChunk(pos.x, pos.z, genLevel.getSeed(), 987234911L);
            slimeChunk = random.nextInt(10) == 0;
        } else {
            slimeChunk = false;
        }
    }
}
