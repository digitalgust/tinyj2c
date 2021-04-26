
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
        return (ch >= 'A' && ch <= 'Z') ? (char) (ch + 32) : ch;
    }

    public static char toUpperCase(char ch) {
        return (ch >= 'a' && ch <= 'z') ? (char) (ch - 32) : ch;
    }

    public static boolean isLowerCase(char ch) {
        return ch >= 'a' && ch <= 'z';
    }
    public static boolean isUpperCase(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }
    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
