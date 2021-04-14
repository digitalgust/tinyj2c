package test;

import java.util.ArrayList;

class Foo3 {

    public static void main(String args[]) {
        long t = System.currentTimeMillis();
        t7();
        System.out.println("spent:" + (System.currentTimeMillis() - t));
    }

    static void t7() {
        final int MAX = 6000000;
        final int PRINT_COUNT = 10000;
        Thread t = new Thread(new Runnable() {
            ArrayList list = new ArrayList(MAX);

            public void run() {

                long start = System.currentTimeMillis();
                System.out.println("thread here.");
                int j = 0;
                String c = null;
                for (int i = 0; i < MAX; i++) {
                    String a = "abc";
//                    String b = "def";
//                    c = a + b;
                    list.add(a);
                    list.remove(0);
                    if (i % PRINT_COUNT == 0) {
                        System.out.println(this + " thread i=" + i);
                    }
                }
                System.out.println(this + " list.size():" + list.size());
                System.out.println(this + " thread cost: " + (System.currentTimeMillis() - start));
            }
        });
        t.start();


        //
        ArrayList list = new ArrayList();
        long start = System.currentTimeMillis();
        String c = null;
        for (int i = 0; i < MAX; i++) {
            String a = "abc";
//            String b = "def";
//            c = a + b;
            list.add(a);
            list.remove(0);
            if (i % PRINT_COUNT == 0) {
                System.out.println("main i=" + i);
            }
        }
        System.out.println("main list.size():" + list.size());
        System.out.println("main thread cost: " + (System.currentTimeMillis() - start));
    }


}
