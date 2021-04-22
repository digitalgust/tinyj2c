
package java.lang.ref;

public class ReferenceQueue<T> {
  private Reference<? extends T> front;

  @SuppressWarnings("unchecked")
  public Reference<? extends T> poll() {
    Reference<? extends T> r = front;
    if (front != null) {
      if (front == front.jNext) {
        front = null;
      } else {
        front = front.jNext;
      }
    }
    return r;
  }

  void add(Reference<? extends T> r) {
    if (front == null) {
      r.jNext = r;
    } else {
      r.jNext = front;
    }
    front = r;
  }
}
