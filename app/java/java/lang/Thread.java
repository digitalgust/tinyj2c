package java.lang;

public class Thread implements Runnable {

    
    private int priority = NORM_PRIORITY;

    
    private Thread threadQ;

    
    private Runnable target;

    
    private char name[];

    
    private long stackFrame = createStackFrame();//vm used
    
    public final static int MIN_PRIORITY = 1;

    
    public final static int NORM_PRIORITY = 5;

    
    public final static int MAX_PRIORITY = 10;


    
    private boolean daemon = false;

    private native long createStackFrame();

    public static native Thread currentThread();

    
    private static int threadInitNumber;

    private static synchronized int nextThreadNum() {
        return ++threadInitNumber;
    }

    
    public static native void yield();

    
    public static native void sleep(long millis) throws InterruptedException;

    
    private void init(Runnable target, String name) {
        Thread parent = currentThread();
        this.target = target;
        this.name = name.toCharArray();
        this.priority = parent.getPriority();
        setPriority0(priority);
    }

    
    public Thread() {
        init(null, "Thread-" + nextThreadNum());
    }

    
    public Thread(String name) {
        init(null, name);
    }

    
    public Thread(Runnable target) {
        init(target, "Thread-" + nextThreadNum());
    }

    
    public Thread(Runnable target, String name) {
        init(target, name);
    }

    
    public synchronized native void start();

    
    public void run() {
        if (target != null) {
            target.run();
        }
    }

    
    public void interrupt() {
        interrupt0();
    }

    
    public final native boolean isAlive();

    
    public final void setPriority(int newPriority) {
        if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
            throw new IllegalArgumentException();
        }
        setPriority0(priority = newPriority);
    }

    
    public final int getPriority() {
        return priority;
    }

    
    public final String getName() {
        return String.valueOf(name);
    }

    
    public final void setName(String tname) {
        if (tname != null) {
            name = tname.toCharArray();
        }
    }

    
    public static native int activeCount();

    
    public synchronized final void join() throws InterruptedException {
        while (isAlive()) {
            wait(1000);
        }
    }

    
    public final void setDaemon(boolean on) {
        if (isAlive()) {
            throw new IllegalThreadStateException();
        }
        daemon = on;
    }

    
    public final boolean isDaemon() {
        return daemon;
    }

    
    public String toString() {
        return "Thread[" + getName() + "," + getPriority() + "]";
    }

    
    private native void setPriority0(int newPriority);

    private native void interrupt0();

}
