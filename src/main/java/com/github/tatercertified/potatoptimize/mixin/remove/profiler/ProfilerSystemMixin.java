package com.github.tatercertified.potatoptimize.mixin.remove.profiler;

import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.SampleType;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(ProfilerSystem.class)
public class ProfilerSystemMixin {

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void startTick() {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void endTick() {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void push(String location) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void push(Supplier<String> locationGetter) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void markSampleType(SampleType type) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void pop() {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void swap(String location) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void swap(Supplier<String> locationGetter) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    private ProfilerSystem.LocatedInfo getCurrentInfo() {
        return null;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void visit(String marker, int num) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public void visit(Supplier<String> markerGetter, int num) {
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public ProfileResult getResult() {
        return null;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public ProfilerSystem.@Nullable LocatedInfo getInfo(String name) {
        return null;
    }

    /**
     * @author QPCrummer
     * @reason Removal
     */
    @Overwrite
    public Set<Pair<String, SampleType>> getSampleTargets() {
        return null;
    }

}
