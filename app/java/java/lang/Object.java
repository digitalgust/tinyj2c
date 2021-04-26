package java.lang;
//     (/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/|[ \t]*//.*)

/**
 * @author gust
 */
public class Object {

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    public final void wait() throws InterruptedException {
        wait(0);
    }

    protected void finalize() throws Throwable {
    }

    public boolean equals(Object obj) {
        return (this == obj);
    }

    protected native Object clone() throws CloneNotSupportedException;

    public final native Class getClass();

    public final native void wait(long ms) throws InterruptedException;

    public final native void notify();

    public final native void notifyAll();

    public native int hashCode();
}
