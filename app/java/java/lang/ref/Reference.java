
package java.lang.ref;

public abstract class Reference<T> {
    private T target; //don't chang the var name ,access by vm
    private ReferenceQueue<? super T> queue;

    Reference jNext;

    protected Reference(T target, ReferenceQueue<? super T> queue) {
        this.target = target;
        this.queue = queue;
    }

    protected Reference(T target) {
        this(target, null);
    }

    public T get() {
        return target;
    }

    public void clear() {
        target = null;
    }

    public boolean isEnqueued() {
        return jNext != null;
    }

    public boolean enqueue() {
        if (queue != null) {
            queue.add(this);
            queue = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * call by vm ,don't delete the method
     */
    static private void vmEnqueneReference(Reference referent) {
        if (referent != null) {
            referent.clear();
            referent.enqueue();
        }
    }

    native void markItAsWeak(boolean weak);
}
