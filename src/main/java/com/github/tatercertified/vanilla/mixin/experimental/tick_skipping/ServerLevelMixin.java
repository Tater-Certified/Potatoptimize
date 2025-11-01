package com.github.tatercertified.vanilla.mixin.experimental.tick_skipping;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * This optimization skips ticking mobs if the server is lagging
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Shadow
    @Final
    private MinecraftServer server;
    private long cachedNSPT;
    private int lastEntityTickIndex = 0;

    private boolean isLagging() {
        long m = Util.getNanos() - this.server.getNextTickTime();
        return m > this.cachedNSPT;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void potatoptimize$assignNSPT(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        this.cachedNSPT = TickThresholdAccessor.getOverloadedThresholdNanos() + 20L *  this.server.tickRateManager().nanosecondsPerTick();
    }

    // TODO Tick players separately
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void potatoptimize$UseAForLoop(EntityTickList instance, Consumer<Entity> consumer) {
        Int2ObjectMap<Entity> entities = ((EntityTickingListAccessor) instance).getActive();
        if (entities.isEmpty()) {
            return;
        }

        int size = entities.size();
        int start = this.lastEntityTickIndex % size;

        for (int i = 0; i < size; i++) {
            int index = (start + i) % size;
            Entity entity = entities.get(i);

            if (entity != null) {
                consumer.accept(entity);
            }

            if (this.isLagging()) {
                this.lastEntityTickIndex = (index + 1) % size;
                return;
            }
        }

        this.lastEntityTickIndex = 0;
    }
}
