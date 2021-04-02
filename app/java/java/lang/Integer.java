
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

import java.lang.String;

/**
 *
 * @author gust
 */
public class Integer {

    private  int value = 0;
    
    public Integer(int p){
        value=p;
    }

    static public String toString(int v) {
        return Long.toString(v);
    }

    public static String toHexString(int v) {
        return Long.toHexString(v);
    }

    public String toString() {
        return toString(value);
    }

}
