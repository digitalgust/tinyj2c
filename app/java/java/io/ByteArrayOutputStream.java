


package java.io;


public class ByteArrayOutputStream extends OutputStream {

    
    protected byte buf[];

    
    protected int count;

    
    private boolean isClosed = false;

    
    private void ensureOpen() {
        if (isClosed) {
            throw new RuntimeException("Writing to closed ByteArrayOutputStream");
        }
    }

    
    public ByteArrayOutputStream() {
        this(32);
    }

    
    public ByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: "
                    + size);
        }
        buf = new byte[size];
    }

    
    public synchronized void write(int b) {
        ensureOpen();
        int newcount = count + 1;
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        buf[count] = (byte) b;
        count = newcount;
    }

    
    public synchronized void write(byte b[], int off, int len) {
        ensureOpen();
        if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        int newcount = count + len;
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        System.arraycopy(b, off, buf, count, len);
        count = newcount;
    }

    
    public synchronized void reset() {
        ensureOpen();
        count = 0;
    }

    
    public synchronized byte toByteArray () 
        [] {
        if (isClosed && buf.length == count) {
            return buf;
        } else {
            byte newbuf[] = new byte[count];
            System.arraycopy(buf, 0, newbuf, 0, count);
            return newbuf;
        }
    }

    
    public int size() {
        return count;
    }

    
    public String toString() {
        return new String(buf, 0, count);
    }

    
    public synchronized void close() throws IOException {
        isClosed = true;
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

}
