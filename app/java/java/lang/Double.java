
package java.lang;

/**
 * @author gust
 */
public class Double {
    @SuppressWarnings("unchecked")
    public static final Class<Double>     TYPE = (Class<Double>) Class.getPrimitiveClass("double");
    public static final double POSITIVE_INFINITY = Double.longBitsToDouble(0x7ff0000000000000L);//1.0 / 0.0;
    public static final double NEGATIVE_INFINITY = Double.longBitsToDouble(0xfff0000000000000L);//-1.0 / 0.0;
    public static final double NaN = Double.longBitsToDouble(0x7ff8000000000000L);//0.0d / 0.0;
    public static final double MAX_VALUE = 1.79769313486231570e+308;
    public static final double MIN_VALUE = 4.94065645841246544e-324;

    private final double value;

    public Double(double value) {
        this.value = value;
    }

    public String toString() {
        return toString(value);
    }

    public static Double valueOf(double i) {
        return new Double(i);
    }

    public double doubleValue() {
        return value;
    }

    static public String toString(double val) {
        return VM.doubleToString(val);
    }

    public static double parseDouble(String s) {
        return VM.stringToDouble(s);
    }

    static public boolean isNaN(double v) {
        return (v != v);
    }

    static public boolean isInfinite(double v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    public static native long doubleToLongBits(double value);

    public static native double longBitsToDouble(long bits);
}
