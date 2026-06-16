/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.vanilla.entity.halloween;

import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import net.minecraft.util.SpecialDates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpecialDates.class)
public class SpecialDatesMixin {
    @Shadow
    @Final
    public static MonthDay HALLOWEEN;
    @Shadow
    @Final
    public static List<MonthDay> CHRISTMAS_RANGE;
    private static boolean isCurrentlyHalloween = true;
    private static boolean isCurrentlyChristmas = true;
    private static long nextHalloweenStart;
    private static long nextHalloweenEnd;
    private static long nextChristmasStart;
    private static long nextChristmasEnd;
    private static final long MS_IN_DAY = 86400000;

    /**
     * @author QPCrummer
     * @reason Use cached values
     */
    @Overwrite
    public static boolean isHalloween() {
        long currentTime = System.currentTimeMillis();
        if (isCurrentlyHalloween) {
            if (currentTime > nextHalloweenEnd) {
                updateHalloweenCache();
            }
        } else if (currentTime > nextHalloweenStart) {
            isCurrentlyHalloween = true;
        }
        return isCurrentlyHalloween;
    }

    /**
     * @author QPCrummer
     * @reason Use cached values
     */
    @Overwrite
    public static boolean isExtendedChristmas() {
        long currentTime = System.currentTimeMillis();
        if (isCurrentlyChristmas) {
            if (currentTime > nextChristmasEnd) {
                updateChristmasCache();
            }
        } else if (currentTime > nextChristmasStart) {
            isCurrentlyChristmas = true;
        }
        return isCurrentlyChristmas;
    }

    private static void updateHalloweenCache() {
        int nextYear = ZonedDateTime.now().getYear() + 1;
        nextHalloweenStart = HALLOWEEN.atYear(nextYear).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        nextHalloweenEnd = nextHalloweenStart + MS_IN_DAY;
        isCurrentlyHalloween = false;
    }

    private static void updateChristmasCache() {
        int nextYear = ZonedDateTime.now().getYear() + 1;
        nextChristmasStart = CHRISTMAS_RANGE.getFirst().atYear(nextYear).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        nextChristmasEnd = nextChristmasStart + MS_IN_DAY * CHRISTMAS_RANGE.size();
        isCurrentlyChristmas = false;
    }
}
