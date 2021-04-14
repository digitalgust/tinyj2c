package java.lang;

import java.io.PrintStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author gust
 */
public class System {
    static final int STD = 0;
    static final int ERR = 1;

    public static PrintStream out = new PrintStream(STD);
    public static PrintStream err = new PrintStream(ERR);

    public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int arr_length);

    public static native long currentTimeMillis();

    public static native long nanoTime();

    static native String doubleToString(double val);

    static native double stringToDouble(String s);

    static native String utf8ToUtf16(byte[] b, int off, int len);

    static native byte[] utf16ToUtf8(String s);
}
