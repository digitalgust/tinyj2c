/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 * @author gust
 */
public class Throwable {
    String detailMessage;

    StackTraceElement backtrace = buildStackElement();

    public Throwable() {
        detailMessage = "";
    }

    public Throwable(String s) {
        detailMessage = s;
    }

    public String getMessage() {
        return detailMessage;
    }

    public String toString() {
        String s = getClass().getName();
        String message = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    public StackTraceElement[] getStackTrace() {

        if (backtrace != null) {
            int count = 0;
            StackTraceElement sf = (StackTraceElement) backtrace;
            while (sf != null) {
                try {
                    Class clazz = Class.forName(sf.getClassName());
                    if (!clazz.isAssignableFrom(Throwable.class)) {
                        count++;
                    }
                } catch (ClassNotFoundException ex) {
                }
                sf = sf.parent;
            }
            StackTraceElement[] arr = new StackTraceElement[count];
            sf = (StackTraceElement) backtrace;
            count = 0;
            while (sf != null) {
                try {
                    Class clazz = Class.forName(sf.getClassName());
                    if (!clazz.isAssignableFrom(Throwable.class)) {
                        arr[count++] = sf;
                    }
                } catch (ClassNotFoundException ex) {
                }
                sf = sf.parent;
            }
            return arr;
        }
        return new StackTraceElement[0];
    }

    public String getCodeStack() {
        StringBuilder stack = new StringBuilder();
        String msg = getMessage();
        stack.append(this.getClass().getName()).append(": ").append(msg == null ? "" : msg).append("\n");
        if (backtrace != null) {
            StackTraceElement sf = (StackTraceElement) backtrace;
            while (sf != null) {
                try {
                    Class clazz = Class.forName(sf.getClassName());
                    if (!clazz.isAssignableFrom(Throwable.class)) {
                        stack.append("    at ").append(sf.getClassName());
                        stack.append(".").append(sf.getMethodName());
                        stack.append("(").append(sf.getFileName());
                        stack.append(":").append(sf.getLineNumber());
                        stack.append(")\n");
                    }
                    sf = sf.parent;
                } catch (Exception e) {
                }
            }
        }
        return stack.toString();
    }

    public void printStackTrace() {
        System.out.print(getCodeStack());

    }

    private native StackTraceElement buildStackElement();
}
