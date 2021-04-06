package java.lang;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.lang.String;

/**
 *
 * @author gust
 */
public class Object {
    
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    public final void wait() throws InterruptedException {
        wait(0);
    }

    public final native Class getClass();
    
    public final native void wait(long ms);

    public final native void notify();

    public final native void notifyAll();

    public native int hashCode();
}
