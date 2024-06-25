package com.github.tatercertified.potatoptimize.utils.interfaces;

public interface InteractionLimiterInterface {
    int getInstaBreakCountPerTick();
    int getPlaceBlockCountPerTick();
    void addInstaBreakCountPerTick();
    void addPlaceBlockCountPerTick();
    boolean allowOperation();
}
