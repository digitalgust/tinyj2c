package java.lang;

/**
 * Some assist method for vm
 */
public class VM {

    public static byte[] toCStyleStr(String s) {
        try {
            byte[] b = s.getBytes("utf-8");
            byte[] b1 = new byte[b.length + 1];
            System.arraycopy(b, 0, b1, 0, b.length);
            return b1;
        } catch (Exception e) {
        }
        return new byte[0];
    }

    static native String doubleToString(double val);

    static native double stringToDouble(String s);

    static native String utf8ToUtf16(byte[] b, int off, int len);

    static native byte[] utf16ToUtf8(String s);
}
