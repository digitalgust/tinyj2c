/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Boolean {
    final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public String toString() {
        return value ? "true" : "false";
    }

    public static boolean parseBoolean(String name) {
        return ((name != null) && name.equalsIgnoreCase("true"));
    }
}
