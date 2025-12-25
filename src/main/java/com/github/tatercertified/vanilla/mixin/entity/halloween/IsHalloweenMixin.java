/**
 * Copyright (c) 2025 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/Potatoptimize/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin.entity.halloween;

import com.github.tatercertified.vanilla.utils.interfaces.HalloweenInterface;
import com.mojang.datafixers.DataFixer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * This optimization only checks for Halloween once when the server starts and stores this value for
 * whenever a Skeleton or Zombie spawns. This optimization has vanilla parity.
 *
 * @author QPCrummer
 * @since 2.0.0
 * @version 1.0.0
 */
@Mixin(MinecraftServer.class)
public abstract class IsHalloweenMixin implements HalloweenInterface {
    @Shadow private long nextTickTimeNanos;
    @Unique private boolean halloween;
    @Unique private boolean nearHalloween;
    @Unique private long waitForHalloween;
    @Unique private long waitForHallowMonth;

    @Inject(
            method = "runServer",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/util/Util;getNanos()J",
                            ordinal = 0,
                            shift = At.Shift.AFTER))
    private void hallowsEveCheck(CallbackInfo ci) {
        checkForHalloween();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void prepareHalloweenCheck(
            Thread thread,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            WorldStem worldStem,
            Proxy proxy,
            DataFixer dataFixer,
            Services services,
            LevelLoadListener levelLoadListener,
            CallbackInfo ci) {
        this.runHalloweenTests();
    }

    @Unique private void checkForHalloween() {
        if (this.nextTickTimeNanos > this.waitForHalloween) {
            this.runHalloweenTests();
        }

        if (this.nextTickTimeNanos > this.waitForHallowMonth) {
            this.runHalloweenTests();
        }
    }

    @Unique private void runHalloweenTests() {
        LocalDateTime today = LocalDateTime.now();
        Month currentMonth = today.getMonth();
        int currentDayOfMonth = today.getDayOfMonth();

        this.nearHalloween =
                (currentMonth == Month.OCTOBER || currentMonth == Month.NOVEMBER)
                        && (currentDayOfMonth >= 20 || currentDayOfMonth <= 3);
        this.halloween = this.nearHalloween && currentDayOfMonth == 31;

        if (this.nearHalloween) {
            this.waitForHallowMonth =
                    this.nextTickTimeNanos
                            + calculateNSTillDate(LocalDate.of(today.getYear(), 11, 4), today)
                            + 50;
        } else {
            this.waitForHallowMonth =
                    this.nextTickTimeNanos
                            + calculateNSTillDate(LocalDate.of(today.getYear() + 1, 10, 20), today)
                            + 50;
        }

        if (this.halloween) {
            this.waitForHalloween =
                    this.nextTickTimeNanos
                            + calculateNSTillDate(LocalDate.of(today.getYear(), 11, 1), today)
                            + 50;
        } else {
            this.waitForHalloween =
                    this.nextTickTimeNanos
                            + calculateNSTillDate(LocalDate.of(today.getYear() + 1, 10, 31), today)
                            + 50;
        }
    }

    @Unique private long calculateNSTillDate(LocalDate date, LocalDateTime current) {
        LocalDateTime targetDateTime = date.atStartOfDay();
        return ChronoUnit.NANOS.between(current, targetDateTime);
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
