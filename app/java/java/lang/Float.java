/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Float {
    public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
    public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
    public static final float NaN = 0.0f / 0.0f;
    public static final float MAX_VALUE = 3.40282346638528860e+38f;
    public static final float MIN_VALUE = 1.40129846432481707e-45f;

    private final float value;

    public Float(float value) {
        this.value = value;
    }

    public String toString() {
        return toString(value);
    }

    public static Float valueOf(float i) {
        return new Float(i);
    }

    public float floatValue() {
        return value;
    }

    static public String toString(float val) {
        return System.doubleToString(val);
    }

    public static float parseFloat(String s) {
        return (float) System.stringToDouble(s);
    }

    static public boolean isNaN(float v) {
        return (v != v);
    }

    static public boolean isInfinite(float v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    public static native int floatToIntBits(float value);

    public static native float intBitsToFloat(int bits);
}
