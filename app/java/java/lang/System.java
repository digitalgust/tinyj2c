package java.lang;

import java.io.ConsoleOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


/**
 * @author gust
 */
public class System {
    static Map<String, String> property = new HashMap<>();

    public static InputStream in = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };
    public static PrintStream out = new PrintStream(new ConsoleOutputStream(ConsoleOutputStream.STD));
    public static PrintStream err = new PrintStream(new ConsoleOutputStream(ConsoleOutputStream.ERR));

    public static String getProperty(String key) {
        return property.get(key);
    }

    public static String setProperty(String key, String value) {
        return property.put(key, value);
    }

    public static void gc() {
        Runtime.getRuntime().gc();
    }

    public static void exit(int code) {
        Runtime.getRuntime().exit(code);
    }

    public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int arr_length);

    public static native long currentTimeMillis();

    public static native long nanoTime();

}
