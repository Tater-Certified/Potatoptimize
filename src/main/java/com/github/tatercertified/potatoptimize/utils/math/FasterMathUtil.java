package com.github.tatercertified.potatoptimize.utils.math;

/**
 * @author jafama library
 * https://github.com/jeffhain/jafama
 */
public final class FasterMathUtil {
    private static final int MAX_FLOAT_EXPONENT = 127;
    private static final int MAX_DOUBLE_EXPONENT = 1023;

    public static int round(float a) {
        final int bits = Float.floatToRawIntBits(a);
        final int biasedExp = ((bits >> 23) & 0xFF);
        final int shift = (23 - 1 + MAX_FLOAT_EXPONENT) - biasedExp;
        if ((shift & -32) == 0) {
            int bitsSignum = (((bits >> 31) << 1) + 1);
            int extendedMantissa = (0x00800000 | (bits & 0x007FFFFF)) * bitsSignum;
            return ((extendedMantissa >> shift) + 1) >> 1;
        } else {
            return (int) a;
        }
    }

    public static long round(double a) {
        final long bits = Double.doubleToRawLongBits(a);
        final int biasedExp = (((int)(bits >> 52)) & 0x7FF);
        final int shift = (52 - 1 + MAX_DOUBLE_EXPONENT) - biasedExp;
        if ((shift & -64) == 0) {
            long bitsSignum = (((bits >> 63) << 1) + 1);
            long extendedMantissa = (0x0010000000000000L | (bits & 0x000FFFFFFFFFFFFFL)) * bitsSignum;
            return ((extendedMantissa >> shift) + 1L) >> 1;
        } else {
            return (long) a;
        }
    }

    public static float positiveModuloForPositiveIntegerDivisor(float dividend, float divisor) {
        var modulo = dividend % divisor;
        return modulo < 0 ? modulo + divisor : modulo;
    }

    public static double positiveModuloForPositiveIntegerDivisor(double dividend, double divisor) {
        var modulo = dividend % divisor;
        return modulo < 0 ? modulo + divisor : modulo;
    }
}
