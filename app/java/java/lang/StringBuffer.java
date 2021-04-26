package java.lang;


/**
 * @author gust
 */
public class StringBuffer extends StringBuilder {

    public StringBuffer() {
        super(16);
    }

    public StringBuffer(int len) {
        super(len);
    }

    synchronized public StringBuffer append(String s) {
        super.append(s);
        return this;
    }

    synchronized public StringBuffer append(double i) {
        super.append(i);
        return this;
    }

    synchronized public StringBuffer append(float i) {
        super.append(i);
        return this;
    }

    synchronized public StringBuffer append(long i) {
        super.append(i);
        return this;
    }

    synchronized public StringBuffer append(int i) {
        super.append(i);
        return this;
    }

    synchronized public StringBuffer append(char c) {
        super.append(c);
        return this;
    }

    synchronized public StringBuffer append(Object o) {
        super.append(o);
        return this;
    }
}
