package com.github.tatercertified.potatoptimize.mixin.entity.halloween;

import com.github.tatercertified.potatoptimize.interfaces.IsHalloweenInterface;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.time.LocalDate;
import java.time.Month;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class IsHalloweenMixin extends World implements IsHalloweenInterface {

    private boolean halloween;
    private boolean nearHalloween;
    private int counter;

    protected IsHalloweenMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getTime()J"))
    private long checkForHalloween(ServerWorld instance) {
        if (counter >= 3600) {
            counter = 0;
            checkForHalloween();
        }
        counter++;
        return this.getTime();
    }

    @Unique
    private void checkForHalloween() {
        LocalDate today = LocalDate.now();
        Month currentMonth = today.getMonth();
        int currentDayOfMonth = today.getDayOfMonth();

        this.nearHalloween = (currentMonth == Month.OCTOBER || currentMonth == Month.NOVEMBER) && (currentDayOfMonth >= 20 || currentDayOfMonth <= 3);
        this.halloween = this.nearHalloween && currentDayOfMonth == 31;
    }

    @Override
    public boolean isHalloween() {
        return this.halloween;
    }

    @Override
    public boolean isNearHalloween() {
        return this.nearHalloween;
    }
}
