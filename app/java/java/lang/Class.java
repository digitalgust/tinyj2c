package java.lang;

public final class Class<T> {
    /*native vm class*/
    long classHandle;

    public native String getName();

    public native Class<? super T> getSuperclass();

    public static native Class<?> forName(String className) throws ClassNotFoundException;

    public native boolean isAssignableFrom(Class cls);

    public native T newInstance() throws InstantiationException;
}