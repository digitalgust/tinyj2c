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
    int stderr;
    static final int NO_NEW_LINE = 0;
    static final int NEW_LINE = 1;

    public PrintStream(int std) {
        stderr = std;
    }

    public void println() {
        printImpl(stderr, null, NEW_LINE);
    }

    public void print(String s) {
        printImpl(stderr, s, NO_NEW_LINE);
    }

    public void println(String s) {
        printImpl(stderr, s, NEW_LINE);
    }

    public void print(int v) {
        printImpl(stderr, Integer.toString(v), NO_NEW_LINE);
    }

    public void println(int v) {
        printImpl(stderr, Integer.toString(v), NEW_LINE);
    }

    public void print(long v) {
        printImpl(stderr, Long.toString(v), NO_NEW_LINE);
    }

    public void println(long v) {
        printImpl(stderr, Long.toString(v), NEW_LINE);
    }

    public void print(float d) {
        printImpl(stderr, Double.toString(d), NO_NEW_LINE);
    }

    public void println(float d) {
        printImpl(stderr, Double.toString(d), NEW_LINE);
    }

    public void print(double d) {
        printImpl(stderr, Double.toString(d), NO_NEW_LINE);
    }

    public void println(double d) {
        printImpl(stderr, Double.toString(d), NEW_LINE);
    }


    static native void printImpl(int stderr, String s, int cr);
}
