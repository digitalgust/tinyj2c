package java.lang;

import java.lang.String;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author gust
 */
public class StringBuilder {

    char[] value = new char[16];
    int count = 0;

    public int length() {
        return count;
    }

    public StringBuilder append(String s) {
        if (s == null) {
            s = "null";
        }
        int len = s.length();
        expand(len);
        s.getChars(0, len, value, count);
        count += len;
        return this;
    }

    public StringBuilder append(double i) {
        return append(Double.toString(i));
    }

    public StringBuilder append(float i) {
        return append(Double.toString(i));
    }

    public StringBuilder append(long i) {
        return append(Long.toString(i));
    }

    public StringBuilder append(int i) {
        return append(Long.toString(i));
    }

    public StringBuilder append(char c) {
        expand(1);
        value[count] = c;
        count++;
        return this;
    }
//=============================================================

    public String toString() {
        return new String(value, 0, count);
    }

    void expand(int need) {
        if (value.length < count + need) {
            char[] v = value;
            value = new char[(count + need) * 2];
            System.arraycopy(v, 0, value, 0, count);
        }
    }

    public void reverse() {
        for (int i = 0; i < count / 2; i++) {
            char ch = value[i];
            value[i] = value[count - 1 - i];
            value[count - 1 - i] = ch;
        }
    }
}
