/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.io;

import java.lang.Integer;
import java.lang.String;

/**
 * @author gust
 */
public class PrintStream {

    public PrintStream() {

    }

    public void println() {
        printImpl(null, 1);
    }

    public void print(String s) {
        printImpl(s, 0);
    }

    public void println(String s) {
        printImpl(s, 1);
    }

    public void print(int v) {
        printImpl(Integer.toString(v), 0);
    }

    public void println(int v) {
        printImpl(Integer.toString(v), 1);
    }

    public void print(long v) {
        printImpl(Long.toString(v), 0);
    }

    public void println(long v) {
        printImpl(Long.toString(v), 1);
    }

    public void print(float d) {
        printImpl(Double.toString(d), 0);
    }

    public void println(float d) {
        printImpl(Double.toString(d), 1);
    }

    public void print(double d) {
        printImpl(Double.toString(d), 0);
    }

    public void println(double d) {
        printImpl(Double.toString(d), 1);
    }


    static native void printImpl(String s, int cr);
}
