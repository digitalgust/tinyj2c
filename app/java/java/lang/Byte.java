/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Byte {
    private final byte value;

    public static final byte MIN_VALUE = -128;
    public static final byte MAX_VALUE = 127;

    public Byte(byte value) {
        this.value = value;
    }

    public String toString() {
        return toString(value);
    }

    public static String toString(byte b) {
        return Long.toString((int) b, 10);
    }


    public static String toString(byte v, int radix) {
        return Long.toString(v, radix);
    }

    public static byte parseByte(String s) {
        return parseByte(s, 10);
    }

    public static byte parseByte(String s, int radix) {
        long i = Long.parseLong(s, radix);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new IllegalArgumentException("Value out of range. Value:\"" + s + "\" Radix:" + radix);
        return (byte) i;
    }
}
