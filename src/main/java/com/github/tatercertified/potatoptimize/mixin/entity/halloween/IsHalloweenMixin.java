package com.github.tatercertified.potatoptimize.mixin.entity.halloween;

import com.github.tatercertified.potatoptimize.utils.interfaces.IsHalloweenInterface;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Mixin(MinecraftServer.class)
public abstract class IsHalloweenMixin implements IsHalloweenInterface {
    @Unique
    private boolean halloween;
    @Unique
    private boolean nearHalloween;
    @Unique
    private long waitForHalloween;
    @Unique
    private long waitForHallowMonth;


    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeNano()J", ordinal = 0, shift = At.Shift.AFTER))
    private void hallowsEveCheck(CallbackInfo ci) {
        checkForHalloween();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void prepareHalloweenCheck(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.runHalloweenTests(Util.getMeasuringTimeNano());
    }

    @Unique
    private void checkForHalloween() {
        long tickStartTimeNanos = Util.getMeasuringTimeNano();

        if (tickStartTimeNanos > this.waitForHalloween) {
            this.runHalloweenTests(tickStartTimeNanos);
        }

        if (tickStartTimeNanos > this.waitForHallowMonth) {
            this.runHalloweenTests(tickStartTimeNanos);
        }
    }

    @Unique
    private void runHalloweenTests(long tickStartTimeNanos) {
        LocalDateTime today = LocalDateTime.now();
        Month currentMonth = today.getMonth();
        int currentDayOfMonth = today.getDayOfMonth();

        this.nearHalloween = (currentMonth == Month.OCTOBER || currentMonth == Month.NOVEMBER) && (currentDayOfMonth >= 20 || currentDayOfMonth <= 3);
        this.halloween = this.nearHalloween && currentDayOfMonth == 31;

        if (this.nearHalloween) {
            this.waitForHallowMonth = tickStartTimeNanos + calculateNSTillDate(LocalDate.of(today.getYear(), 11, 4), today) + 50;
        } else {
            this.waitForHallowMonth = tickStartTimeNanos + calculateNSTillDate(LocalDate.of(today.getYear() + 1, 10, 20), today) + 50;
        }

        if (this.halloween) {
            this.waitForHalloween = tickStartTimeNanos + calculateNSTillDate(LocalDate.of(today.getYear(), 11, 1), today) + 50;
        } else {
            this.waitForHalloween = tickStartTimeNanos + calculateNSTillDate(LocalDate.of(today.getYear() + 1, 10, 31), today) + 50;
        }
    }

    @Unique
    private long calculateNSTillDate(LocalDate date, LocalDateTime current) {
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
