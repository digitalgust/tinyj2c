
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

import java.lang.String;

/**
 * @author gust
 */
public class Integer {
    public static final int   MIN_VALUE = 0x80000000;
    public static final int   MAX_VALUE = 0x7fffffff;

    private int value;

    public Integer(int p) {
        value = p;
    }

    static public String toString(int v) {
        return Long.toString(v);
    }

    public static String toHexString(int v) {
        return Long.toString(v, 16);
    }

    public String toString() {
        return toString(value);
    }

    static public String toString(int v, int radix) {
        return Long.toString(v, 10);
    }

    public static int parseInt(String s) {
        return (int) Long.parseLong(s, 10);
    }

    public static int parseInt(String s, int radix) {
        return (int) Long.parseLong(s, radix);
    }

}
