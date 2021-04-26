package java.io;

public class PrintStream extends OutputStream {


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

    public void write(int b) {
        try {
            synchronized (this) {
                out.write(b);
            }
        } catch (IOException x) {
        }
    }

    public void write(byte buf[], int off, int len) {
        try {
            synchronized (this) {
                out.write(buf, off, len);
            }
        } catch (IOException x) {
        }
    }

    public void println() {
        newLine();
    }

    public void print(String s) {
        if (s == null) s = "null";
        try {

            byte[] b = s.getBytes("utf-8");
            synchronized (this) {
                out.write(b);
            }
        } catch (Exception e) {
        }
    }

    public void println(String s) {
        print(s);
        newLine();
    }

    public void print(Object o) {
        print(o == null ? "null" : o.toString());
    }

    public void println(Object o) {
        print(o == null ? "null" : o.toString());
        newLine();
    }

    public void print(int v) {
        print(Integer.toString(v));
    }

    public void println(int v) {
        print(Integer.toString(v));
        newLine();
    }

    public void print(char v) {
        write(v);
    }

    public void println(char v) {
        write(v);
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
