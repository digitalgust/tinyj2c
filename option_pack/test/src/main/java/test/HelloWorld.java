package test;

public class HelloWorld {

    void test() throws RuntimeException {
        try {
            System.out.println("Hello world!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new HelloWorld().test();
    }
}
