package com.github.tatercertified.potatoptimize.mixin.random.world;

import com.github.tatercertified.potatoptimize.utils.interfaces.LightningInterface;
import com.github.tatercertified.potatoptimize.utils.interfaces.SnowInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class RandomServerWorldMixin extends World implements SnowInterface {

    private int currentIceAndSnowTick;

    protected RandomServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Override
    public void resetIceAndSnowTick() {
        this.currentIceAndSnowTick = this.random.nextInt(16);
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I", ordinal = 1))
    private int redirectIceAndSnowChance(Random instance, int i) {
        return this.currentIceAndSnowTick;
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isThundering()Z"))
    private boolean redirectIsThundering(ServerWorld instance, @Local(ordinal = 0, argsOnly = true) WorldChunk chunk) {
        return instance.isThundering() && ((LightningInterface)chunk).shouldDoLightning(this.random);
    }
}
