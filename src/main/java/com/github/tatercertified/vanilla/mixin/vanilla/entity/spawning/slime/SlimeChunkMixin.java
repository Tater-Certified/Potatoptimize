/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.vanilla.entity.spawning.slime;

import com.github.tatercertified.vanilla.utils.interfaces.SlimeChunkInterface;
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

    @Inject(
            method =
                    "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;)V",
            at = @At("TAIL"))
    private void assignSlimeChunk1(Level level, ChunkPos pos, CallbackInfo ci) {
        setSlimeChunk(level, pos);
    }

    @Inject(
            method =
                    "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V",
            at = @At("TAIL"))
    private void assignSlimeChunk2(
            ServerLevel level,
            ProtoChunk protoChunk,
            LevelChunk.PostLoadProcessor postLoad,
            CallbackInfo ci) {
        setSlimeChunk(level, protoChunk.getPos());
    }

    @Inject(
            method =
                    "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V",
            at = @At("TAIL"))
    private void assignSlimeChunk3(
            Level level,
            ChunkPos pos,
            UpgradeData upgradeData,
            LevelChunkTicks blockTicks,
            LevelChunkTicks fluidTicks,
            long inhabitedTime,
            LevelChunkSection[] sections,
            LevelChunk.PostLoadProcessor postLoad,
            BlendingData blendingData,
            CallbackInfo ci) {
        setSlimeChunk(level, pos);
    }

    @Override
    public boolean isSlimeChunk() {
        return slimeChunk;
    }

    private void setSlimeChunk(Level world, ChunkPos pos) {
        if (world instanceof WorldGenLevel genLevel) {
            RandomSource random =
                    WorldgenRandom.seedSlimeChunk(pos.x(), pos.z(), genLevel.getSeed(), 987234911L);
            slimeChunk = random.nextInt(10) == 0;
        } else {
            slimeChunk = false;
        }
    }
}
