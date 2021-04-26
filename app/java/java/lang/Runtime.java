



package java.lang;



public class Runtime {
    private static Runtime currentRuntime = new Runtime();

    
    public static Runtime getRuntime() { 
        return currentRuntime;
    }

    
    private Runtime() {}

    
    private native void exitInternal(int status);

    
    public void exit(int status) {
        exitInternal(status);
    }

    
    public native long freeMemory();

    
    public native long totalMemory();

    
    public native void gc();

}

