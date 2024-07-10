package com.github.tatercertified.potatoptimize.mixin.random.slime;

import com.github.tatercertified.potatoptimize.utils.interfaces.SlimeChunkInterface;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class SlimeChunkMixin implements SlimeChunkInterface {

    private boolean slimeChunk;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;)V", at = @At("TAIL"))
    private void assignSlimeChunk1(World world, ChunkPos pos, CallbackInfo ci) {
        setSlimeChunk(world, pos);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V", at = @At("TAIL"))
    private void assignSlimeChunk2(ServerWorld world, ProtoChunk protoChunk, WorldChunk.EntityLoader entityLoader, CallbackInfo ci) {
        setSlimeChunk(world, protoChunk.getPos());
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/UpgradeData;Lnet/minecraft/world/tick/ChunkTickScheduler;Lnet/minecraft/world/tick/ChunkTickScheduler;J[Lnet/minecraft/world/chunk/ChunkSection;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;Lnet/minecraft/world/gen/chunk/BlendingData;)V", at = @At("TAIL"))
    private void assignSlimeChunk3(World world, ChunkPos pos, UpgradeData upgradeData, ChunkTickScheduler blockTickScheduler, ChunkTickScheduler fluidTickScheduler, long inhabitedTime, ChunkSection[] sectionArrayInitializer, WorldChunk.EntityLoader entityLoader, BlendingData blendingData, CallbackInfo ci) {
        setSlimeChunk(world, pos);
    }

    @Override
    public boolean isSlimeChunk() {
        return slimeChunk;
    }

    private void setSlimeChunk(World world, ChunkPos pos) {
        if (world instanceof StructureWorldAccess) {
            Random random = ChunkRandom.getSlimeRandom(pos.x, pos.z, ((StructureWorldAccess)world).getSeed(), 987234911L);
            slimeChunk = random.nextInt(10) == 0;
        } else {
            slimeChunk = false;
        }
    }
}
