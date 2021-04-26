package java.lang;


/**
 * @author gust
 */
public class StringBuilder {

    char[] value;
    int count = 0;

    public StringBuilder() {
        this(16);
    }

    public StringBuilder(String s) {
        if (s == null) {
            s = "null";
        }
        value = new char[s.length()];
        System.arraycopy(s.value, 0, value, 0, value.length);
    }

    public StringBuilder(int len) {
        value = new char[len];
    }

    public int length() {
        return count;
    }

    public StringBuilder append(String s) {
        if (s == null) {
            s = "null";
        }
        int len = s.length();
        expand(len);
        s.getChars(0, len, value, count);
        count += len;
        return this;
    }

    public StringBuilder append(double i) {
        return append(Double.toString(i));
    }

    public StringBuilder append(float i) {
        return append(Double.toString(i));
    }

    public StringBuilder append(long i) {
        return append(Long.toString(i));
    }

    public StringBuilder append(int i) {
        return append(Long.toString(i));
    }

    public StringBuilder append(char c) {
        expand(1);
        value[count] = c;
        count++;
        return this;
    }

    public StringBuilder append(Object o) {
        if (o != null) append(o.toString());
        else append("null");
        return this;
    }
//=============================================================

    public String toString() {
        return new String(value, 0, count);
    }

    void expand(int need) {
        if (value.length < count + need) {
            char[] v = value;
            value = new char[(count + need) * 2];
            System.arraycopy(v, 0, value, 0, count);
        }
    }

    public void reverse() {
        for (int i = 0; i < count / 2; i++) {
            char ch = value[i];
            value[i] = value[count - 1 - i];
            value[count - 1 - i] = ch;
        }
    }
}
