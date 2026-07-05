/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.vanilla.entity.halloween;

import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import net.minecraft.util.SpecialDates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void potatoptimize$initCache(CallbackInfo ci) {
        ZonedDateTime now = ZonedDateTime.now();

        // Init Halloween cache
        if (isBefore(now, HALLOWEEN)) {
            nextHalloweenStart = getTimeStamp(now.getYear(), HALLOWEEN, true);
            nextHalloweenEnd = getTimeStamp(now.getYear(), HALLOWEEN, false);
            isCurrentlyHalloween = false;
        } else if (isDuring(now, HALLOWEEN, 0)){
            nextHalloweenStart = getTimeStamp(now.getYear(), HALLOWEEN, true);
            nextHalloweenEnd = getTimeStamp(now.getYear(), HALLOWEEN, false);
        } else {
            updateHalloweenCache();
        }

        // Init Christmas cache
        if (isBefore(now, CHRISTMAS_RANGE.getFirst())) {
            nextChristmasStart = getTimeStamp(now.getYear(), CHRISTMAS_RANGE.getFirst(), true);
            nextChristmasEnd = getTimeStamp(now.getYear(), CHRISTMAS_RANGE.getLast(), false);
            isCurrentlyChristmas = false;
        } else if (isDuring(now, CHRISTMAS_RANGE.getFirst(), CHRISTMAS_RANGE.size() - 1)) {
            nextChristmasStart = getTimeStamp(now.getYear(), CHRISTMAS_RANGE.getFirst(), true);
            nextChristmasEnd = getTimeStamp(now.getYear(), CHRISTMAS_RANGE.getLast(), false);
        } else {
            updateChristmasCache();
        }
    }

    private static void updateHalloweenCache() {
        int nextYear = ZonedDateTime.now().getYear() + 1;
        nextHalloweenStart = getTimeStamp(nextYear, HALLOWEEN, true);
        nextHalloweenEnd = getTimeStamp(nextYear, HALLOWEEN, false);
        isCurrentlyHalloween = false;
    }

    private static void updateChristmasCache() {
        int nextYear = ZonedDateTime.now().getYear() + 1;
        nextChristmasStart = getTimeStamp(nextYear, CHRISTMAS_RANGE.getFirst(), true);
        nextChristmasEnd = getTimeStamp(nextYear, CHRISTMAS_RANGE.getLast(), false);
        isCurrentlyChristmas = false;
    }

    private static boolean isBefore(ZonedDateTime currentTime, MonthDay checkingAgainst) {
        return currentTime.getDayOfYear() < checkingAgainst.atYear(currentTime.getYear()).getDayOfYear();
    }

    private static boolean isDuring(ZonedDateTime currentTime, MonthDay checkingAgainstFirstDay, int eventAdditionalDays) {
        int eventFirstDay = checkingAgainstFirstDay.atYear(currentTime.getYear()).getDayOfYear();
        int eventLastDay = eventFirstDay + eventAdditionalDays;
        int currentDay = currentTime.getDayOfYear();
        return currentDay >= eventFirstDay && currentDay <= eventLastDay;
    }

    private static long getTimeStamp(int referenceYear, MonthDay day, boolean startOfDay) {
        return startOfDay ? day.atYear(referenceYear).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() : day.atYear(referenceYear).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
