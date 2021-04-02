/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.io;

import java.lang.Integer;
import java.lang.String;

/**
 *
 * @author gust
 */
public class PrintStream {

    public PrintStream() {

    }

    public void print(String s) {
        printImpl(s);
    }

    public void println(String s) {
        printImpl(s + "\n");
    }

    public void print(int v) {
        printImpl(Integer.toString(v));
    }

    public void println(int v) {
        printImpl(Integer.toString(v) + "\n");
    }

    public void print(long v) {
        printImpl(Long.toString(v));
    }

    public void println(long v) {
        printImpl(Long.toString(v) + "\n");
    }

    public void print(float d) {
        printImpl(Double.toString(d));
    }

    public void println(float d) {
        printImpl(Double.toString(d) + "\n");
    }

    public void print(double d) {
        printImpl(Double.toString(d));
    }

    public void println(double d) {
        printImpl(Double.toString(d) + "\n");
    }

    public void println() {
        printImpl(new String("\n"));
    }

//    void printImpl(String s){
//        java.lang.System.out.println("");
//    }
    static native void printImpl(String s);
}
