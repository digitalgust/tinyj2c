/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Double {
    public static final double POSITIVE_INFINITY = 1.0 / 0.0;
    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;
    public static final double NaN = 0.0d / 0.0;
    public static final double MAX_VALUE = 1.79769313486231570e+308;
    public static final double MIN_VALUE = 4.94065645841246544e-324;

    private final double value;

    public Double(double value) {
        this.value = value;
    }

    public String toString() {
        return toString(value);
    }

    static public String toString(double val) {
        return System.doubleToString(val);
    }

    public static double parseDouble(String s) {
        return System.stringToDouble(s);
    }

    static public boolean isNaN(double v) {
        return (v != v);
    }

    static public boolean isInfinite(double v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

}
