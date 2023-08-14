package com.github.tatercertified.potatoptimize.mixin.memory.memory_reserve;

import net.minecraft.util.crash.CrashMemoryReserve;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CrashMemoryReserve.class)
public class CrashMemoryReserveMixin {
    /**
     * @author QPCrummer
     * @reason Removal of Memory Reserve
     */
    @Overwrite
    public static void reserveMemory() {
    }

    /**
     * @author QPCrummer
     * @reason Removal of Memory Reserve
     */
    @Overwrite
    public static void releaseMemory() {
    }
}
