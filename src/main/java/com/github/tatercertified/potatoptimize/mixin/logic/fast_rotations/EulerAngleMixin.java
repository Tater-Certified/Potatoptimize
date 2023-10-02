package com.github.tatercertified.potatoptimize.mixin.logic.fast_rotations;

import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Credit to PaperMC patch #0565
 */
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

    @Inject(method = "<init>(FFF)V", at = @At("HEAD"), cancellable = true)
    private static void fastConstructor(float pitch1, float yaw1, float roll1, CallbackInfo ci) {
        pitch = pitch1;
        yaw = yaw1;
        roll = roll1;
        ci.cancel();
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
