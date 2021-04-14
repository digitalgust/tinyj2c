/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Long {
    @SuppressWarnings("unchecked")
    public static final Class<Long> TYPE = (Class<Long>) Class.getPrimitiveClass("long");
    public static final long MIN_VALUE = 0x8000000000000000L;
    public static final long MAX_VALUE = 0x7fffffffffffffffL;

    long value;

    public Long(long p) {
        value = p;
    }

    static public String toString(long v) {
        return toString(v, 10);
    }

    public static Long valueOf(long l) {
        return new Long(l);
    }

    public long longValue() {
        return (long) value;
    }

    static public String toString(long v, int radix) {

        if (radix >= DIGI.length) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        boolean neg = false;
        if (v < 0) {
            neg = true;
            v = -v;
        }
        while (true) {
            long n = v % radix;
            sb.append(DIGI[(int) n]);
            v = v / radix;
            if (v == 0) {
                break;
            }
        }
        if (neg) sb.append('-');
        sb.reverse();
        return sb.toString();
    }

    public static String toHexString(long v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int idx = (int) (v >>> ((16 - 1 - i) * 4)) & 0xf;
            if (idx == 0) continue;
            else sb.append(DIGI[idx]);
        }
        return sb.toString();
    }

    static char[] DIGI = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    static private int indexOfDigi(char ch) {
        ch = Character.toUpperCase(ch);
        for (int i = 0, imax = DIGI.length; i < imax; i++) {
            if (DIGI[i] == ch) {
                return i;
            }
        }
        return -1;
    }

    public static long parseLong(String s, int radix) {
        long v = 0;
        boolean neg = false;
        int idx = 0;
        if (s.length() > 0) {
            if (s.value[idx] == '-') {
                neg = true;
                idx++;
            }
        }
        for (int i = idx, imax = s.length(); i < imax; i++) {
            int ci = indexOfDigi(s.value[i]);
            if (ci >= 0 && ci < radix) {
                v = v * radix + ci;
            } else {
                throw new IllegalArgumentException();
            }
        }
        return neg ? -v : v;
    }


    public String toString() {
        return toString(value);
    }

}
