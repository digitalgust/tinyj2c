package java.lang.reflect;

public class Constructor<T> extends Method {

    public T newInstance(Object... initargs) {
        T obj = newInstanceWithoutInit(clazz);
        invoke(obj, initargs);
        return obj;
    }

    static native <T> T newInstanceWithoutInit(Class cl);
}
