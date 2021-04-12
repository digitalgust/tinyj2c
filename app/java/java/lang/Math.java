/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Math {

    private Math() {
    }

    public static final double E = 2.7182818284590452354;

    public static final double PI = 3.14159265358979323846;

    public static native double sin(double a);

    public static native double cos(double a);

    public static native double tan(double a);

    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * PI;
    }

    public static double toDegrees(double angrad) {
        return angrad * 180.0 / PI;
    }

    public static native double sqrt(double a);

    public static native double ceil(double a);

    public static native double exp(double a);

    public static native double floor(double a);

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }

    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }

    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    private static long negativeZeroFloatBits = Float.floatToIntBits(-0.0f);
    private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0d);

    public static float max(float a, float b) {
        if (a != a) {
            return a; // a is NaN
        }
        if ((a == 0.0f) && (b == 0.0f)
                && (Float.floatToIntBits(a) == negativeZeroFloatBits)) {
            return b;
        }
        return (a >= b) ? a : b;
    }

    public static double max(double a, double b) {
        if (a != a) {
            return a; // a is NaN
        }
        if ((a == 0.0d) && (b == 0.0d)
                && (Double.doubleToLongBits(a) == negativeZeroDoubleBits)) {
            return b;
        }
        return (a >= b) ? a : b;
    }

    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    public static long min(long a, long b) {
        return (a <= b) ? a : b;
    }

    public static float min(float a, float b) {
        if (a != a) {
            return a; // a is NaN
        }
        if ((a == 0.0f) && (b == 0.0f)
                && (Float.floatToIntBits(b) == negativeZeroFloatBits)) {
            return b;
        }
        return (a <= b) ? a : b;
    }

    public static double min(double a, double b) {
        if (a != a) {
            return a; // a is NaN
        }
        if ((a == 0.0d) && (b == 0.0d)
                && (Double.doubleToLongBits(b) == negativeZeroDoubleBits)) {
            return b;
        }
        return (a <= b) ? a : b;
    }

    public static double signum(double d) {
        if (Double.isNaN(d)) return d;
        if (Double.doubleToLongBits(d) == negativeZeroDoubleBits) return d;
        if (Double.doubleToLongBits(d) == 0.0d) return d;
        if (d > 0) return 1.0d;
        else return -1.0d;
    }

    public static float signum(float f) {
        if (Float.isNaN(f)) return f;
        if (Float.floatToIntBits(f) == negativeZeroFloatBits) return f;
        if (Float.floatToIntBits(f) == 0.0f) return f;
        if (f > 0) return 1.0f;
        else return -1.0f;
    }

    public static long round(double a) {
        return (long) floor(a + 0.5d);
    }

    public static int round(float a) {
        return (int) floor(a + 0.5f);
    }

    public static native double asin(double a);

    public static native double acos(double a);

    public static native double atan(double a);

    public static native double log(double a);

    public static native double atan2(double y, double x);

    public static native double pow(double a, double b);
}
