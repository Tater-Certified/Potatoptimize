package com.github.tatercertified.potatoptimize.mixin.logic.fast_rotations;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Credit to PaperMC patch #0565
 */
//TODO Rewrite this so that pitch, yaw, and roll can be inlined properly
@Mixin(EulerAngle.class)
public class EulerAngleMixin {

    @Mutable
    @Unique
    @Final
    private static float pitch;
    @Unique
    @Final
    @Mutable
    private static float yaw;
    @Unique
    @Final
    @Mutable
    private static float roll;

    @Redirect(method = "<init>(FFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/EulerAngle;pitch:F"))
    private void injectedPitch(EulerAngle instance, float value, @Local(ordinal = 0) float pitch) {
        EulerAngleMixin.pitch = pitch;
    }

    @Redirect(method = "<init>(FFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/EulerAngle;yaw:F"))
    private void injectedYaw(EulerAngle instance, float value, @Local(ordinal = 0) float yaw) {
        EulerAngleMixin.yaw = yaw;
    }

    @Redirect(method = "<init>(FFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/EulerAngle;roll:F"))
    private void injectedRoll(EulerAngle instance, float value, @Local(ordinal = 0) float roll) {
        EulerAngleMixin.roll = roll;
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public float getPitch() {
        return pitch;
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public float getYaw() {
        return yaw;
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public float getRoll() {
        return roll;
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public float getWrappedPitch() {
        return MathHelper.wrapDegrees(pitch);
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public float getWrappedYaw() {
        return MathHelper.wrapDegrees(yaw);
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public float getWrappedRoll() {
        return MathHelper.wrapDegrees(roll);
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public boolean equals(Object o) {
        if (!(o instanceof EulerAngle eulerAngle)) {
            return false;
        } else {
            return pitch == eulerAngle.getPitch() && yaw == eulerAngle.getYaw() && roll == eulerAngle.getRoll();
        }
    }

    /**
     * @author QPCrummer
     * @reason make fast
     */
    @Overwrite
    public NbtList toNbt() {
        NbtList nbtList = new NbtList();
        nbtList.add(NbtFloat.of(pitch));
        nbtList.add(NbtFloat.of(yaw));
        nbtList.add(NbtFloat.of(roll));
        return nbtList;
    }
}
