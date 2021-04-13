package java.lang.reflect;

public class Field {
    /*native vm methodinfo*/
    private long fieldHandle;

    Class<?> clazz;
    String name;
    String desc;
    String signature;

    public String getName() {
        return name;
    }

}
