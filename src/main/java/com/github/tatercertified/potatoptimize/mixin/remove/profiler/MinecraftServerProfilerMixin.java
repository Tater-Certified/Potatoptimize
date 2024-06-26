package com.github.tatercertified.potatoptimize.mixin.remove.profiler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MinecraftServer.class)
public class MinecraftServerProfilerMixin {
    /**
     * @author QPCrummer
     * @reason Remove Profiler
     */
    @Overwrite
    public Profiler getProfiler() {
        return DummyProfiler.INSTANCE;
    }
}
