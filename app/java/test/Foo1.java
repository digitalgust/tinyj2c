package test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Foo1 {
//


    static class Tclass {

        public String getName(Object o1, int i, byte b, String a, Object o) {
            return "NameClass/" + o1 + "/" + i + "/" + b + "/" + a + "/" + o;
        }
    }


    void testChinese() {
        String s = "中国";
        System.out.println(s);
        byte[] b = s.getBytes("utf-8");
        if (b != null) {
            System.out.println(s + " bytes:" + b.length);
            String s1 = new String(b, 0, b.length, "utf-8");
            System.out.println(s1);
        }
    }


    void testThreadWait() {
        final Object lock = new Object();
        Thread t = new Thread(() -> {
            System.out.println("Second Thread start.");
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println("count= " + i);
                    Thread.sleep(100);
                    if (i == 5) {
                        synchronized (lock) {
                            lock.notify();
                            System.out.println("second notify.");
                        }
                    }
                }
            } catch (Exception e) {
            }
            System.out.println("Second Thread over.");
        });
        t.start();
        try {
            synchronized (lock) {
                lock.wait();
                System.out.println("Main thread weakup.");
            }
        } catch (Exception e) {
        }
    }


    public void testReflect() {
        try {
            Class c = Class.forName("test.Foo1.Tclass");
            Object o = c.newInstance();
            Method m = c.getMethod("t2");
            m.invoke(o);
            m = c.getMethod("getName", Object.class, int.class, byte.class, String.class, Object.class);
            if (m != null) {
                System.out.println("invoke " + m.invoke(o, this, 2, 4, "Here is ok", o));
            }
            System.out.println("forName and newInstance :" + o);

        } catch (Exception e) {
        }
    }

    public void testContainer() {
        try {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) list.add(i);
            System.out.println("List size:" + list.size() + " elementAt (4) =" + list.indexOf(4));

            Map<Integer, String> map = new HashMap<>();
            for (int i = 0; i < 10; i++) map.put(i, "value-" + i);
            System.out.println("Map size:" + map.size() + " key(4) =" + map.get(4));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testSpeed() {
        final int MAX = 30000000;
        final int PRINT_COUNT = 10000;
        Thread t = new Thread(new Runnable() {
            ArrayList list = new ArrayList();

            public void run() {

                long start = System.currentTimeMillis();
                System.out.println("thread here.");
                int j = 0;
                String c = null;
                for (int i = 0; i < MAX; i++) {
                    String a = "abc";
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
            list.add(a);
            list.remove(0);
            if (i % PRINT_COUNT == 0) {
                System.out.println("main i=" + i);
            }
        }
        System.out.println("main list.size():" + list.size());
        System.out.println("main thread cost: " + (System.currentTimeMillis() - start));
    }

    public static void main(String args[]) {
        Foo1 obj = new Foo1();
        obj.testChinese();
        obj.testThreadWait();
        obj.testReflect();
        obj.testContainer();
        obj.testSpeed();
    }
}
