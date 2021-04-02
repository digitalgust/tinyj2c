/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.test;

/**
 * @author gust
 */

class P {
    public int p_ins_var = 6;
    static public int p_static_var;
    int x;

    P() {
        x = p();
    }

    static public int p() {
        return 65;
    }

    int getX() {
        return x;
    }

}

public class Foo2 extends P {

    int i;
    static int si;
    int[] arr = new int[]{5, 6};
    short s;
    byte b;
    Object o;
    char c;
    double d;
    float f;
    long l = 0x1000000120000002L;
    String str;

    public String getName(Object o1, int i, byte b, String a, Object o) {
        return "NameClass";
    }

    public int getValue() {
        return i;
    }

    public void t1() {
        System.out.println("start ...");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            si += Math.min(i, 10000);
        }
        long end = System.currentTimeMillis();
        System.out.println("spent ms :" + (end - start));
        System.out.println("si =" + si);
    }

    public void t2() {
        Foo2 foo1 = new Foo2();
        foo1.i = 90;
        foo1.str = foo1.getName(foo1.o, foo1.i, foo1.b, "", foo1.o);
        foo1.p_ins_var = 66;
        p_static_var = 67;
        foo1.arr[1] = 7;
        for (int k = 0; k < foo1.arr.length; k++) {
            System.out.println("arr[" + k + "]=" + foo1.arr[k]);
        }

        System.out.println(Long.toHexString(foo1.l));
        System.out.println(foo1.str);
        System.out.println(foo1.getX());
        foo1.t1();
    }

    public void t3() {

        si = 65;

        System.out.println("P.p()=" + p());
        System.out.println("helloworld");

        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());

        System.out.println("p.p_static_var=" + p_static_var);
        P.p_static_var = 5;
        System.out.println(p_static_var);

    }

    public void t4() {
        int x = (int) (42 + 1);
        int y = 6345;
        int c = 0;
        int d = 23456;
        int f = 0;
        System.out.println("abc".lastIndexOf(""));
        System.out.println("--------------------");
        System.out.println("initial value ");
        System.out.println("random number x : " + x);
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("c = " + c);
        System.out.println("d = " + d);
        System.out.println("f = " + f);
        System.out.println("--------------------");

        c = x + y;
        d += c;
        System.out.println("c = x + y = " + x + " + " + y + " = " + c);
        System.out.println("d = d + c = " + d);
        x = c / 2;
        System.out.println("x = c/2 = " + x);

        c = x * y;
        d += c;
        System.out.println("c = x * y = " + x + " * " + y + " = " + c);
        System.out.println("d = d + c = " + d);
        x = c / 2;
        System.out.println("x = c/2 = " + x);

        c = x - y;
        d += c;
        System.out.println("c = x - y = " + x + " - " + y + " = " + c);
        System.out.println("d = d + c = " + d);
        x = c / 2;
        System.out.println("x = c/2 = " + x);

        c = x / y;
        d += c;
        System.out.println("c = x / y = " + x + " / " + y + " = " + c);
        System.out.println("d = d + c = " + d);

        f = d + x + y + c;
        System.out.println("f = " + (d) + " + " + (x) + " + " + (y) + " + " + (c) + " = " + f);
        System.out.println("Foo Test ");

    }

    public void t5() {
        String[] strs = new String[10];
        for (int i = 0; i < strs.length; i++) {
            strs[i] = "" + i;
        }
        System.out.println("strs.length=" + strs.length);
        for (int i = 0; i < strs.length; i++) {
            System.out.println("strs[" + i + "]=" + strs[i]);
        }
    }

    public void t6() {

        double f0 = 1.5f;
        double f1 = f0;

        double[] val = new double[10];
        f1 = 5 % f0 - 0.6f * (0.5f + 5) / 9.8f;
        System.out.println(f1);
        val[0] = f1;
        System.out.println(val[0]);

    }

    public static void main(String args[]) {
        Foo2 obj = new Foo2();
        obj.t1();
        obj.t2();
        obj.t3();
        obj.t4();
        obj.t5();
        obj.t6();
    }

}
