
package java.lang;

/**
 * @author gust
 */
public class Boolean {
    @SuppressWarnings("unchecked")
    public static final Class TYPE = Class.getPrimitiveClass("boolean");
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public String toString() {
        return value ? "true" : "false";
    }

    public boolean booleanValue() {
        return value;
    }

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

    public static boolean parseBoolean(String name) {
        return ((name != null) && name.equalsIgnoreCase("true"));
    }
}
