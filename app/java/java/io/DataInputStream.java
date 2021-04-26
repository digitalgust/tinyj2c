
package java.io;



public
class DataInputStream extends InputStream  {

    
    protected InputStream in;

    
    public DataInputStream(InputStream in) {
        this.in = in;
    }

    
    public int read() throws IOException {
        return in.read();
    }

    
    public final int read(byte b[]) throws IOException {
        return in.read(b, 0, b.length);
    }

    
    public final int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    
    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    
    public final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

    
    public final int skipBytes(int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total<n) && ((cur = (int) skip(n-total)) > 0)) {
            total += cur;
        }
        return total;
    }

    
    public final boolean readBoolean() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }

    
    public final byte readByte() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)(ch);
    }

    
    public final int readUnsignedByte() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    
    public final short readShort() throws IOException {
        return (short)readUnsignedShort();
    }

    
    public final int readUnsignedShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
             throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    
    public final char readChar() throws IOException {
        return (char)readUnsignedShort();
    }

    
    public final int readInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
             throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    
    public final long readLong() throws IOException {
        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    
    public final String readUTF() throws IOException {
        return readUTF(this);
    }

    
    public final static String readUTF(DataInputStream in) throws IOException {
        int utflen = in.readUnsignedShort();
        char str[] = new char[utflen];
        byte bytearr [] = new byte[utflen];
        int c, char2, char3;
        int count = 0;
        int strlen = 0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    
                    count++;
                    str[strlen++] = (char)c;
                    break;
                case 12: case 13:
                    
                    count += 2;
                    if (count > utflen)
                        throw new RuntimeException("UTFDataFormatException");
                    char2 = (int) bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new RuntimeException("UTFDataFormatException");
                    str[strlen++] = (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14:
                    
                    count += 3;
                    if (count > utflen)
                        throw new RuntimeException("UTFDataFormatException");
                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new RuntimeException("UTFDataFormatException");
                    str[strlen++] = (char)(((c     & 0x0F) << 12) |
                                           ((char2 & 0x3F) << 6)  |
                                           ((char3 & 0x3F) << 0));
                    break;
                default:
                    
                    throw new RuntimeException("UTFDataFormatException");
                }
        }

        return new String(str, 0, strlen);
    }

    
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    
    public int available() throws IOException {
        return in.available();
    }

    
    public void close() throws IOException {
        in.close();
    }

    
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    
    public synchronized void reset() throws IOException {
        in.reset();
    }

    
    public boolean markSupported() {
        return in.markSupported();
    }
}

