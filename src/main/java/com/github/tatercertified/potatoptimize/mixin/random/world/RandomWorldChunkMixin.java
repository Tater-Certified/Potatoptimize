package com.github.tatercertified.potatoptimize.mixin.random.world;

import com.github.tatercertified.potatoptimize.utils.interfaces.LightningInterface;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public abstract class RandomWorldChunkMixin extends Chunk implements LightningInterface {

    @Shadow @Final World world;

    public RandomWorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    private int lightningTick;

    @Override
    public boolean shouldDoLightning(Random random) {
        if (this.lightningTick-- <= 0) {
            this.lightningTick = random.nextInt(100000) << 1;
            return true;
        }
        return false;
    }

    @IfModAbsent(value = "c2me-base")
    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/UpgradeData;Lnet/minecraft/world/tick/ChunkTickScheduler;Lnet/minecraft/world/tick/ChunkTickScheduler;J[Lnet/minecraft/world/chunk/ChunkSection;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;Lnet/minecraft/world/gen/chunk/BlendingData;)V", at = @At("TAIL"))
    private void injectRandomLightningTick(World world, ChunkPos pos, UpgradeData upgradeData, ChunkTickScheduler blockTickScheduler, ChunkTickScheduler fluidTickScheduler, long inhabitedTime, ChunkSection[] sectionArrayInitializer, WorldChunk.EntityLoader entityLoader, BlendingData blendingData, CallbackInfo ci) {
        this.lightningTick = this.world.getRandom().nextInt(100000) << 1;
    }
}
