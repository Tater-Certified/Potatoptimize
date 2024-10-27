package com.github.tatercertified.potatoptimize.mixin.remove.profiler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Profilers.class)
public class MinecraftServerProfilerMixin {
    /**
     * @author QPCrummer
     * @reason Remove Profiler
     */
    @Overwrite
    public static Profiler get() {
        return DummyProfiler.INSTANCE;
    }
}
