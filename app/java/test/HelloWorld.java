package test;

public class HelloWorld {
    static String str = "Hello world!";

    void test() throws RuntimeException {
        try {
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new HelloWorld().test();
    }
}
