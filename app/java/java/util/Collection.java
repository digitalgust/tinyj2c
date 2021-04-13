

package java.util;

public interface Collection<E> extends Iterable<E> {
    // Query Operations

    int size();

    boolean isEmpty();

    boolean contains(Object o);

    Iterator<E> iterator();

    Object[] toArray();

    <T> T[] toArray(T[] a);

    // Modification Operations
    boolean add(E o);

    boolean remove(Object o);

    // Bulk Operations
    boolean containsAll(Collection<?> c);

    boolean addAll(Collection<? extends E> c);

    boolean removeAll(Collection<?> c);


    boolean retainAll(Collection<?> c);

    void clear();

    // Comparison and hashing
    boolean equals(Object o);

    int hashCode();


}
