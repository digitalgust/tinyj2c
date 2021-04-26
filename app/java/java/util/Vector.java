


package java.util;

public class Vector<E>
        extends AbstractList<E>
        implements List<E>, Cloneable {


    protected Object[] elementData;


    protected int elementCount;


    protected int capacityIncrement;




    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "
                    + initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }


    public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }


    public Vector() {
        this(10);
    }


    public Vector(Collection<? extends E> c) {
        elementCount = c.size();

        elementData = new Object[(int) Math.min((elementCount * 110L) / 100, Integer.MAX_VALUE)];
        c.toArray(elementData);
    }


    public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }


    public synchronized void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (elementCount < oldCapacity) {
            Object oldData[] = elementData;
            elementData = new Object[elementCount];
            System.arraycopy(oldData, 0, elementData, 0, elementCount);
        }
    }


    public synchronized void ensureCapacity(int minCapacity) {
        modCount++;
        ensureCapacityHelper(minCapacity);
    }


    private void ensureCapacityHelper(int minCapacity) {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            Object[] oldData = elementData;
            int newCapacity = (capacityIncrement > 0)
                    ? (oldCapacity + capacityIncrement) : (oldCapacity * 2);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = new Object[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, elementCount);
        }
    }


    public synchronized void setSize(int newSize) {
        modCount++;
        if (newSize > elementCount) {
            ensureCapacityHelper(newSize);
        } else {
            for (int i = newSize; i < elementCount; i++) {
                elementData[i] = null;
            }
        }
        elementCount = newSize;
    }


    public synchronized int capacity() {
        return elementData.length;
    }


    public synchronized int size() {
        return elementCount;
    }


    public synchronized boolean isEmpty() {
        return elementCount == 0;
    }


    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;

            public boolean hasMoreElements() {
                return count < elementCount;
            }

            public E nextElement() {
                synchronized (Vector.this) {
                    if (count < elementCount) {
                        return (E) elementData[count++];
                    }
                }
                throw new NoSuchElementException();
            }
        };
    }


    public boolean contains(Object elem) {
        return indexOf(elem, 0) >= 0;
    }


    public int indexOf(Object elem) {
        return indexOf(elem, 0);
    }


    public synchronized int indexOf(Object elem, int index) {
        if (elem == null) {
            for (int i = index; i < elementCount; i++) {
                if (elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = index; i < elementCount; i++) {
                if (elem.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }


    public synchronized int lastIndexOf(Object elem) {
        return lastIndexOf(elem, elementCount - 1);
    }


    public synchronized int lastIndexOf(Object elem, int index) {
        if (index >= elementCount) {
            throw new IndexOutOfBoundsException(index + " >= " + elementCount);
        }

        if (elem == null) {
            for (int i = index; i >= 0; i--) {
                if (elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = index; i >= 0; i--) {
                if (elem.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }


    public synchronized E elementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        }

        return (E) elementData[index];
    }


    public synchronized E firstElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return (E) elementData[0];
    }


    public synchronized E lastElement() {
        if (elementCount == 0) {
            throw new NoSuchElementException();
        }
        return (E) elementData[elementCount - 1];
    }


    public synchronized void setElementAt(E obj, int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= "
                    + elementCount);
        }
        elementData[index] = obj;
    }


    public synchronized void removeElementAt(int index) {
        modCount++;
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= "
                    + elementCount);
        } else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        elementCount--;
        elementData[elementCount] = null;

    }


    public synchronized void insertElementAt(E obj, int index) {
        modCount++;
        if (index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index
                    + " > " + elementCount);
        }
        ensureCapacityHelper(elementCount + 1);
        System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
        elementData[index] = obj;
        elementCount++;
    }


    public synchronized void addElement(E obj) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = obj;
    }


    public synchronized boolean removeElement(Object obj) {
        modCount++;
        int i = indexOf(obj);
        if (i >= 0) {
            removeElementAt(i);
            return true;
        }
        return false;
    }


    public synchronized void removeAllElements() {
        modCount++;

        for (int i = 0; i < elementCount; i++) {
            elementData[i] = null;
        }

        elementCount = 0;
    }


    public synchronized Object clone() {
        try {
            Vector<E> v = (Vector<E>) super.clone();
            System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
            v.modCount = 0;
            return v;
        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("clone error");
        }
    }


    public synchronized Object[] toArray() {
        Object[] result = new Object[elementCount];
        System.arraycopy(elementData, 0, result, 0, elementCount);
        return result;
    }


    public synchronized <T> T[] toArray(T[] a) {
        if (a.length < elementCount)
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), elementCount);

        System.arraycopy(elementData, 0, a, 0, elementCount);

        if (a.length > elementCount) {
            a[elementCount] = null;
        }

        return a;
    }


    public synchronized E get(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return (E) elementData[index];
    }


    public synchronized E set(int index, E element) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        Object oldValue = elementData[index];
        elementData[index] = element;
        return (E) oldValue;
    }


    public synchronized boolean add(E o) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = o;
        return true;
    }


    public boolean remove(Object o) {
        return removeElement(o);
    }


    public void add(int index, E element) {
        insertElementAt(element, index);
    }


    public synchronized E remove(int index) {
        modCount++;
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Object oldValue = elementData[index];

        int numMoved = elementCount - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index,
                    numMoved);
        }
        elementData[--elementCount] = null;

        return (E) oldValue;
    }


    public void clear() {
        removeAllElements();
    }


    public synchronized boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }


    public synchronized boolean addAll(Collection<? extends E> c) {
        modCount++;
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);
        System.arraycopy(a, 0, elementData, elementCount, numNew);
        elementCount += numNew;
        return numNew != 0;
    }


    public synchronized boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }


    public synchronized boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }


    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        modCount++;
        if (index < 0 || index > elementCount) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);

        int numMoved = elementCount - index;
        if (numMoved > 0) {
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);
        }

        System.arraycopy(a, 0, elementData, index, numNew);
        elementCount += numNew;
        return numNew != 0;
    }


    public synchronized boolean equals(Object o) {
        return super.equals(o);
    }


    public synchronized int hashCode() {
        return super.hashCode();
    }


    public synchronized String toString() {
        return super.toString();
    }


}
