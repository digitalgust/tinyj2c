package java.util;

public class Hashtable<K, V> {

    private final HashMap<K, V> map;

    public Hashtable(int capacity) {
        map = new HashMap(capacity);
    }

    public Hashtable(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    public Hashtable() {
        this(0);
    }

    public synchronized String toString() {
        return map.toString();
    }

    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized boolean contains(Object value) {
        return map.containsValue(value);
    }

    public synchronized boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public synchronized boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public synchronized V get(Object key) {
        return map.get(key);
    }

    public synchronized V put(K key, V value) {
        return map.put(key, value);
    }

    public synchronized void putAll(Map<? extends K, ? extends V> elts) {
        map.putAll(elts);
    }

    public synchronized V remove(Object key) {
        return map.remove(key);
    }

    public synchronized void clear() {
        map.clear();
    }

    public Enumeration<K> keys() {
        return new Enumeration<K>() {
            Object[] list = map.keySet().toArray();
            int eindex = 0;

            @Override
            public boolean hasMoreElements() {
                return list != null && eindex < list.length;
            }

            @Override
            public K nextElement() {
                return (K) list[eindex++];
            }
        };
    }

    public Enumeration<V> elements() {
        return new Enumeration<V>() {
            Object[] list = map.values().toArray();
            int eindex = 0;

            @Override
            public boolean hasMoreElements() {
                return list != null && eindex < list.length;
            }

            @Override
            public V nextElement() {
                return (V) list[eindex++];
            }
        };
    }
}

