

package java.lang.reflect;


public final class Array {
    static int[] dim = {0};

    private Array() {
    }


    public static Object newInstance(Class<?> componentType, int length) {
        synchronized (dim) {
            dim[0] = length;
            return multiNewArray(componentType, dim);
        }
    }


    public static Object newInstance(Class<?> componentType, int[] dimensions)
            throws IllegalArgumentException {
        return multiNewArray(componentType, dimensions);
    }


    static native Object multiNewArray(Class componentType, int[] dimensions);
}
