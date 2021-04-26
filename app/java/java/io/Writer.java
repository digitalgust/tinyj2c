


package java.io;


public abstract class Writer {


    private char[] writeBuffer;


    private final int writeBufferSize = 1024;


    protected Object lock;


    protected Writer() {
        this.lock = this;
    }


    protected Writer(Object lock) {
        if (lock == null) {
            throw new NullPointerException();
        }
        this.lock = lock;
    }


    public void write(int c) throws IOException {
        synchronized (lock) {
            if (writeBuffer == null) {
                writeBuffer = new char[writeBufferSize];
            }
            writeBuffer[0] = (char) c;
            write(writeBuffer, 0, 1);
        }
    }


    public void write(char cbuf[]) throws IOException {
        write(cbuf, 0, cbuf.length);
    }


    abstract public void write(char cbuf[], int off, int len) throws IOException;


    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }


    public void write(String str, int off, int len) throws IOException {
        synchronized (lock) {
            char cbuf[];
            if (len <= writeBufferSize) {
                if (writeBuffer == null) {
                    writeBuffer = new char[writeBufferSize];
                }
                cbuf = writeBuffer;
            } else {
                cbuf = new char[len];
            }
            str.getChars(off, (off + len), cbuf, 0);
            write(cbuf, 0, len);
        }
    }


    abstract public void flush() throws IOException;


    abstract public void close() throws IOException;

    public Writer append(final char c) throws IOException {
        write((int) c);
        return this;
    }

    public Writer append(final CharSequence sequence) throws IOException {
        return append(sequence, 0, sequence.length());
    }

    public Writer append(CharSequence sequence, int start, int end)
            throws IOException {
        final int length = end - start;
        if (sequence instanceof String) {
            write((String) sequence, start, length);
        } else {
            final char[] charArray = new char[length];
            for (int i = start; i < end; i++) {
                charArray[i] = sequence.charAt(i);
            }
            write(charArray, 0, length);
        }
        return this;
    }

}
