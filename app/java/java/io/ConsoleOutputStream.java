package java.io;

public class ConsoleOutputStream extends OutputStream {
    public static final int STD = 0;
    public static final int ERR = 1;
    int stderr;

    public ConsoleOutputStream(int stderr) {
        this.stderr = stderr;
    }

    static native void writeImpl(int stderr, int b);

    @Override
    public void write(int b) throws IOException {
        writeImpl(stderr, b);
    }
}
