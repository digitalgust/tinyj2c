/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Character {
    @SuppressWarnings("unchecked")
    public static final Class<Character> TYPE = (Class<Character>) Class.getPrimitiveClass("char");

    char value;

    public Character(char ch) {
        value = ch;
    }

    public char charValue() {
        return value;
    }

    public static Character valueOf(char i) {
        return new Character(i);
    }

    public static char toLowerCase(char ch) {
        return (ch >= 65 && ch <= 90) ? (char) (ch + 32) : ch;
    }

    public static char toUpperCase(char ch) {
        return (ch >= 97 && ch <= 122) ? (char) (ch - 32) : ch;
    }
}
