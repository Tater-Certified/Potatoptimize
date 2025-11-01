package com.github.tatercertified.vanilla.mixin.experimental.tick_skipping;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface TickThresholdAccessor {
    @Accessor("OVERLOADED_THRESHOLD_NANOS")
    static long getOverloadedThresholdNanos() {
        throw new AssertionError();
    }
}
