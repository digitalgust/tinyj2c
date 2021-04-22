package java.lang;

import java.io.ConsoleOutputStream;
import java.io.PrintStream;


/**
 * @author gust
 */
public class System {


    public static PrintStream out = new PrintStream(new ConsoleOutputStream(ConsoleOutputStream.STD));
    public static PrintStream err = new PrintStream(new ConsoleOutputStream(ConsoleOutputStream.ERR));

    public static native void gc();

    public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int arr_length);

    public static native long currentTimeMillis();

    public static native long nanoTime();

    static native String doubleToString(double val);

    static native double stringToDouble(String s);

    static native String utf8ToUtf16(byte[] b, int off, int len);

    static native byte[] utf16ToUtf8(String s);
}
