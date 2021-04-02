package java.lang;

import java.io.PrintStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author gust
 */
public class System {

    public static PrintStream out = new PrintStream();

    public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int arr_length);

    public static native long currentTimeMillis();

    public static native long nanoTime();

    static native String doubleToString(double val);
}
