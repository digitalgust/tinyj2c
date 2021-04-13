
package java.lang;

public
class ArrayIndexOutOfBoundsException extends RuntimeException {
    public ArrayIndexOutOfBoundsException() {
        super();
    }

    public ArrayIndexOutOfBoundsException(String s) {
        super(s);
    }

    public ArrayIndexOutOfBoundsException(int index) {
        super(Integer.toString(index));
    }
}

