package java.io;

import java.lang.Integer;
import java.lang.String;

public class PrintStream {


    OutputStream out;

    public PrintStream(OutputStream out) {
        this.out = out;
    }

    private void newLine() {
        try {
            out.write('\n');
        } catch (Exception e) {
        }
    }

    public void println() {
        newLine();
    }

    public void print(String s) {
        if (s == null) s = "null";
        try {
            byte[] b = s.getBytes("utf-8");
            out.write(b);
        } catch (Exception e) {
        }
    }

    public void println(String s) {
        print(s);
        newLine();
    }

    public void print(int v) {
        print(Integer.toString(v));
    }

    public void println(int v) {
        print(Integer.toString(v));
        newLine();
    }

    public void print(long v) {
        print(Long.toString(v));
    }

    public void println(long v) {
        print(Long.toString(v));
        newLine();
    }

    public void print(float d) {
        print(Double.toString(d));
    }

    public void println(float d) {
        print(Double.toString(d));
        newLine();
    }

    public void print(double d) {
        print(Double.toString(d));
    }

    public void println(double d) {
        print(Double.toString(d));
        newLine();
    }

    public void flush() {
        try {
            out.flush();
        } catch (Exception e) {
        }
    }

    public void close() {
        try {
            out.close();
        } catch (Exception e) {
        }
    }

}
