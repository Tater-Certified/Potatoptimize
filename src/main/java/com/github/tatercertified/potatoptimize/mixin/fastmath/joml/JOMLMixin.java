package com.github.tatercertified.potatoptimize.mixin.fastmath.joml;

import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Math.class, remap = false)
public class JOMLMixin {
    @Redirect(method = "<clinit>", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMath() {
        return true;
    }

    @Redirect(method = "<clinit>", at = @At(value = "FIELD", target = "Lorg/joml/Options;SIN_LOOKUP:Z"))
    private static boolean redirectSinLookup() {
        return true;
    }

    @Redirect(method = "sin(F)F", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup1() {
        return true;
    }

    @Redirect(method = "sin(F)F", at = @At(value = "FIELD", target = "Lorg/joml/Options;SIN_LOOKUP:Z"))
    private static boolean redirectSinLookup1() {
        return true;
    }

    @Redirect(method = "sin(D)D", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup2() {
        return true;
    }

    @Redirect(method = "sin(D)D", at = @At(value = "FIELD", target = "Lorg/joml/Options;SIN_LOOKUP:Z"))
    private static boolean redirectSinLookup2() {
        return true;
    }

    @Redirect(method = "cos(F)F", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup3() {
        return true;
    }

    @Redirect(method = "cos(D)D", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup4() {
        return true;
    }

    @Redirect(method = "cosFromSin(FF)F", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup5() {
        return true;
    }

    @Redirect(method = "cosFromSin(DD)D", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup6() {
        return true;
    }

    @Redirect(method = "atan2(DD)D", at = @At(value = "FIELD", target = "Lorg/joml/Options;FASTMATH:Z"))
    private static boolean redirectFastMathLookup7() {
        return true;
    }
}
