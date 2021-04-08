package java.lang;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author gust
 */
public class StringBuffer extends StringBuilder {

    public StringBuffer() {
        super(16);
    }

    synchronized public StringBuffer append(String s) {
         super.append(s);
         return this;
    }

    synchronized public StringBuffer append(double i) {
         super.append(i);
         return this;
    }

    synchronized public StringBuffer append(float i) {
         super.append(i);
         return this;
    }

    synchronized public StringBuffer append(long i) {
         super.append(i);
         return this;
    }

    synchronized public StringBuffer append(int i) {
         super.append(i);
         return this;
    }

    synchronized public StringBuffer append(char c) {
         super.append(c);
         return this;
    }

    synchronized public StringBuffer append(Object o) {
         super.append(o);
         return this;
    }
}
