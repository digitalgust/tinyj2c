
package java.lang;

import java.lang.String;

/**
 * @author gust
 */
public class Integer {
    @SuppressWarnings("unchecked")
    public static final Class<Integer> TYPE = (Class<Integer>) Class.getPrimitiveClass("int");
    public static final int MIN_VALUE = 0x80000000;
    public static final int MAX_VALUE = 0x7fffffff;

    static Integer[] cache;
    static int low, high;

    static {
        // high value may be configured by property
        int h = 127;

        high = h;

        cache = new Integer[(high - low) + 1];
        int j = low;
        for (int k = 0; k < cache.length; k++)
            cache[k] = new Integer(j++);

        // range [-128, 127] must be interned (JLS7 5.1.7)
    }

    private int value;

    public Integer(int p) {
        value = p;
    }

    static public String toString(int v) {
        return Long.toString(v);
    }

    public static Integer valueOf(int i) {
        if (i >= low && i <= high)
            return cache[i + (-low)];
        return new Integer(i);
    }

    public int intValue() {
        return value;
    }

    public static String toHexString(int v) {
        return Long.toHexString(((long) v) & 0xffffffffL);
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
