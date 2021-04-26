package java.io;

public class RandomAccessFile {

    private static final int O_RDONLY = 1;
    private static final int O_RDWR = 2;
    private static final int O_SYNC = 4;
    private static final int O_DSYNC = 8;
    private boolean rw;

    String path, mode;
    long filePointer;
    boolean flush = false;


    public RandomAccessFile(String name, String mode)
            throws IOException {
        int imode = -1;
        if (mode.equals("r")) {
            imode = O_RDONLY;
        } else if (mode.startsWith("rw")) {
            imode = O_RDWR;
            rw = true;
            if (mode.length() > 2) {
                if (mode.equals("rws")) {
                    imode |= O_SYNC;
                } else if (mode.equals("rwd")) {
                    imode |= O_DSYNC;
                } else {
                    imode = -1;
                }
            }
        }
        if (imode < 0) {
            throw new IllegalArgumentException("Illegal mode \"" + mode
                    + "\" must be one of "
                    + "\"r\", \"rw\", \"rws\","
                    + " or \"rwd\"");
        }
        if (name == null) {
            throw new NullPointerException();
        }
        init(name, mode);
    }

    private void init(String ppath, String pmode) throws IOException {
        //System.out.println("pmode:" + pmode);
        this.path = ppath;
        if ("r".equals(pmode)) {
            this.mode = "rb";
        } else if ("rw".equals(pmode)) {
            this.mode = "rb+";
        } else if ("rws".equals(pmode)) {
            this.mode = "rb+";
            flush = true;
        } else if ("rwd".equals(pmode)) {
            this.mode = "rb+";
            flush = true;
        } else {
            this.mode = "rb+";
        }
        filePointer = openFile(VM.toCStyleStr(path), VM.toCStyleStr(mode));
        if (filePointer == 0 && "rb+".equals(this.mode)) {// file not exists , create new
            this.mode = "wb+";
            filePointer = openFile(VM.toCStyleStr(path), VM.toCStyleStr(mode));
        }
        if (filePointer == 0) {
            throw new IOException("open file error:" + path);
        }
    }


    public int read() throws IOException {
        return read0(filePointer);
    }

    private int readBytes(byte[] b, int off, int len) throws IOException {
        return readbuf(filePointer, b, off, len);
    }


    public int read(byte[] b, int off, int len) throws IOException {
        return readBytes(b, off, len);
    }


    public int read(byte[] b) throws IOException {
        return readBytes(b, 0, b.length);
    }


    public final void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }


    public final void readFully(byte[] b, int off, int len) throws IOException {
        int n = 0;
        do {
            int count = readbuf(filePointer, b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        } while (n < len);
    }


    public int skipBytes(int n) throws IOException {
        long pos;
        long len;
        long newpos;

        if (n <= 0) {
            return 0;
        }
        pos = getFilePointer();
        len = length();
        newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        seek(newpos);


        return (int) (newpos - pos);
    }


    public void write(int b) throws IOException {
        write0(filePointer, b);
    }


    private void writeBytes(byte[] b, int off, int len) throws IOException {
        int wrote = 0;
        int ret = 0;
        while ((ret = writebuf(filePointer, b, wrote + off, len - wrote)) < len) {

            if (ret < 0) {
                throw new IOException("write file error.");
            }
            wrote += ret;

        }
        if (flush) {
            flush0(getFilePointer());
        }
    }


    public void write(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }


    public void write(byte[] b, int off, int len) throws IOException {
        writeBytes(b, off, len);
    }


    public long getFilePointer() throws IOException {
        return filePointer;
    }


    public void seek(long pos) throws IOException {
        seek0(filePointer, pos);
    }


    public long length() throws IOException {
        return length0(filePointer);
    }


    public void setLength(long newLength) throws IOException {
        setLength0(filePointer, newLength);
    }


    public void close() throws IOException {
        closeFile(filePointer);
    }


    public final boolean readBoolean() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }


    public final byte readByte() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) (ch);
    }


    public final int readUnsignedByte() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }


    public final short readShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + (ch2 << 0));
    }


    public final int readUnsignedShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }


    public final char readChar() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char) ((ch1 << 8) + (ch2 << 0));
    }


    public final int readInt() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }


    public final long readLong() throws IOException {
        return ((long) (readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }


    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }


    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }


    public final String readLine() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = read()) {
                case -1:
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    long cur = getFilePointer();
                    if ((read()) != '\n') {
                        seek(cur);
                    }
                    break;
                default:
                    input.append((char) c);
                    break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }


    public final String readUTF() throws IOException {
        return DataInputStream.readUTF(new DataInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return RandomAccessFile.this.read();
            }
        }));
    }


    public final void writeBoolean(boolean v) throws IOException {
        write(v ? 1 : 0);

    }


    public final void writeByte(int v) throws IOException {
        write(v);

    }


    public final void writeShort(int v) throws IOException {
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);

    }


    public final void writeChar(int v) throws IOException {
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);

    }


    public final void writeInt(int v) throws IOException {
        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);

    }


    public final void writeLong(long v) throws IOException {
        write((int) (v >>> 56) & 0xFF);
        write((int) (v >>> 48) & 0xFF);
        write((int) (v >>> 40) & 0xFF);
        write((int) (v >>> 32) & 0xFF);
        write((int) (v >>> 24) & 0xFF);
        write((int) (v >>> 16) & 0xFF);
        write((int) (v >>> 8) & 0xFF);
        write((int) (v >>> 0) & 0xFF);

    }


    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }


    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }


    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        byte[] b
                = s.getBytes();
        writeBytes(b, 0, len);
    }


    public final void writeChars(String s) throws IOException {
        int clen = s.length();
        int blen = 2 * clen;
        byte[] b = new byte[blen];
        char[] c = new char[clen];
        s.getChars(0, clen, c, 0);
        for (int i = 0, j = 0; i < clen; i++) {
            b[j++] = (byte) (c[i] >>> 8);
            b[j++] = (byte) (c[i] >>> 0);
        }
        writeBytes(b, 0, blen);
    }


    public final void writeUTF(String str) throws IOException {
        DataOutputStream.writeUTF(str, new DataOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                RandomAccessFile.this.write(b);
            }
        }));
    }

    public void flush() {
        flush0(filePointer);
    }


    public static native long openFile(byte[] filePath, byte[] mode);

    public static native int closeFile(long fileHandle);

    public static native int flush0(long fileHandle);

    public static native int read0(long fileHandle);

    public static native int write0(long fileHandle, int b);

    public static native int readbuf(long fileHandle, byte[] b, int off, int len);

    public static native int writebuf(long fileHandle, byte[] b, int off, int len);

    public static native int available0(long fileHandle);

    public static native int seek0(long fileHandle, long pos);

    public static native int setLength0(long fileHandle, long len);

    public static native int length0(long fileHandle);

}
