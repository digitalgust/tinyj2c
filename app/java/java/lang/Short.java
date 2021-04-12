/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Short {
    public static final short MIN_VALUE = -32768;
    public static final short MAX_VALUE = 32767;


    final short value;

    public Short(short p) {
        value = p;
    }

    public static Short valueOf(short s) {
        return new Short(s);
    }

    public short shortValue() {
        return value;
    }

    static public String toString(short v) {
        return Long.toString(v);
    }

    public static String toHexString(short v) {
        return Long.toString(v, 16);
    }

    public String toString() {
        return toString(value);
    }

    static public String toString(short v, int radix) {
        return Long.toString(v, 10);
    }

    public static int parseShort(String s) {
        return (int) Long.parseLong(s, 10);
    }

    public static int parseShort(String s, int radix) {
        return (int) Long.parseLong(s, radix);
    }

}
