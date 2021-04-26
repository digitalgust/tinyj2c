package java.lang;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Class<T> {
    private static final int ENUM = 0x00004000;

    /*native vm class*/
    long classHandle;
    String name;
    int modifiers;
    Constructor<?>[] constructors;
    Field[] fields;
    Method[] methods;

    public Constructor<?>[] getConstructors() {
        if (fields == null) {
            initReflect(this);
        }
        return constructors;
    }

    public Field[] getFields() {
        if (fields == null) {
            initReflect(this);
        }
        return fields;
    }

    public Field getField(String name) {
        Field[] ms = getFields();
        for (int i = 0, imax = ms.length; i < imax; i++) {
            if (ms[i].getName().equals(name)) {
                return ms[i];
            }
        }
        return null;
    }

    public Method[] getMethods() {
        if (fields == null) {
            initReflect(this);
        }
        return methods;
    }

    @SuppressWarnings("unchecked")
    public Method getMethod(String name, Class<?>... parameterTypes) {
        Method[] ms = getMethods();
        for (int i = 0, imax = ms.length; i < imax; i++) {
            if (ms[i].match(name, parameterTypes)) {
                return ms[i];
            }
        }
        return null;
    }

    public T[] getEnumConstants() {
        T[] values = getEnumConstantsShared();
        return (values != null) ? values.clone() : null;
    }

    @SuppressWarnings("unchecked")
    T[] getEnumConstantsShared() {
        if (enumConstants == null) {
            if (!isEnum()) {
                return null;
            }
            try {
                final Method values = getMethod("values");
                T[] temporaryConstants = (T[]) values.invoke(null);
                enumConstants = temporaryConstants;
            } // These can happen when users concoct enum-like classes
            // that don't comply with the enum spec.
            catch (Exception ex) {
                return null;
            }
        }
        return enumConstants;
    }

    private volatile transient T[] enumConstants = null;

    Map<String, T> enumConstantDirectory() {
        if (enumConstantDirectory == null) {
            T[] universe = getEnumConstantsShared();
            if (universe == null) {
                throw new IllegalArgumentException(getName() + " is not an enum type");
            }
            Map<String, T> m = new HashMap<>(2 * universe.length);
            for (T constant : universe) {
                m.put(((Enum<?>) constant).name(), constant);
            }
            enumConstantDirectory = m;
        }
        return enumConstantDirectory;
    }

    private volatile transient Map<String, T> enumConstantDirectory = null;

    public Class getComponentType() {
        if (isArray()) {
            String n = getName();
            n = n.substring(1);
            return getClassByDescriptor(n);
        } else {
            return null;
        }
    }

    static public Class getClassByDescriptor(String s) {
        switch (s.charAt(0)) {
            case 'S':
                return Short.TYPE;
            case 'C':
                return Character.TYPE;
            case 'B':
                return Byte.TYPE;
            case 'I':
                return Integer.TYPE;
            case 'F':
                return Float.TYPE;
            case 'Z':
                return Boolean.TYPE;
            case 'D':
                return Double.TYPE;
            case 'J':
                return Long.TYPE;
            case 'V':
                return Void.TYPE;
            case 'L':
                if (s.indexOf('<') >= 0) {
                    s = s.substring(1, s.indexOf('<'));
                } else {
                    s = s.substring(1, s.length() - 1);
                }//no break here
            default:
                try {
                    return Class.forName(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
        }
    }

    public InputStream getResourceAsStream(String path) {
        return null;
    }


    public native String getName();

    public native Class<? super T> getSuperclass();

    public static native Class<?> forName(String className) throws ClassNotFoundException;

    public native boolean isAssignableFrom(Class cls);

    public native boolean isArray();

    public native boolean isInterface();

    public boolean isEnum() {
        return (this.modifiers & ENUM) != 0 && this.getSuperclass() == java.lang.Enum.class;
    }

    public native boolean isInstance(Object obj);

    public native boolean isPrimitive();

    public native T newInstance() throws InstantiationException;

    static native Class<?> getPrimitiveClass(String name);

    static native void initReflect(Class clazz);


}