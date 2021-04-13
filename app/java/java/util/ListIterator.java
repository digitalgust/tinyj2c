


package java.util;


public interface ListIterator<E> extends Iterator<E> {


    boolean hasNext();


    E next();


    boolean hasPrevious();


    E previous();


    int nextIndex();


    int previousIndex();


    void remove();


    void set(E o);


    void add(E o);
}
