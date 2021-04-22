
package java.lang.ref;

public class WeakReference<T> extends Reference<T> {
  public WeakReference(T target, ReferenceQueue<? super T> queue) {
    super(target, queue);
    markItAsWeak(true);
  }

  public WeakReference(T target) {
    this(target, null);
  }
}
