/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 *
 * @author gust
 */
public class Long {

    private long value = 0;

    public Long(long p) {
        value = p;
    }

    static public String toString(long v) {
        StringBuilder sb = new StringBuilder();
        boolean neg = false;
        if (v < 0) {
            neg = true;
            v = -v;
        }
        while (true) {
            long n = v % 10;
            sb.append("0123456789".charAt((int) n));
            v = v / 10;
            if (v == 0) {
                break;
            }
        }
        if (neg) {
            sb.append('-');
        }
        sb.reverse();
        return sb.toString();
    }

    public static String toHexString(long v) {
        StringBuilder sb = new StringBuilder();
        if (v < 0) {
            v = -v;
        }
        while (true) {
            long n = v % 16;
            sb.append("0123456789ABCDEF".charAt((int) n));
            v = v / 16;
            if (v == 0) {
                break;
            }
        }
        sb.reverse();
        return sb.toString();
    }

    public String toString() {
        return toString(value);
    }

}
