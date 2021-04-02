/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 *
 * @author gust
 */
public final class String {

    int hash = 0;
    private final char[] value;
    int offset,count;

    public String() {
        this.value = new char[0];
    }

    public String(char[] buf) {
        value = new char[buf.length];
        count =  buf.length;
        System.arraycopy(buf, 0, value, 0, buf.length);
    }

    public String(char[] v, int offset, int count) {
        if (offset < 0 || count < 0 || offset > v.length - count) {
            this.value = new char[0];
            return;
        }
        value = new char[count];
        this.count = count;
        System.arraycopy(v, offset, this.value, 0, count);
    }

    public String(String original) {
        this.value = original.value;
        this.hash = original.hash;
    }

    public int length() {
        return value.length;
    }

    public boolean isEmpty() {
        return value.length == 0;
    }

    public char charAt(int index) {
        if ((index < 0) || (index >= value.length)) {
           throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
        }
        return value[index];
    }

    public char[] toCharArray() {
        char result[] = new char[value.length];
        getChars(0, value.length, result, 0);
        return result;
    }

    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0 || srcEnd > value.length || srcBegin > srcEnd) {
            return;
        }
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            String anotherString = (String) anObject;
            int n = value.length;
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i]) {
                        return false;
                    }
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    public int compareTo(String anotherString) {
        int len1 = value.length;
        int len2 = anotherString.value.length;
        int lim = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = anotherString.value;

        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    public boolean startsWith(String prefix, int toffset) {
        char ta[] = value;
        int to = toffset;
        char pa[] = prefix.value;
        int po = 0;
        int pc = prefix.value.length;
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > value.length - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    public boolean endsWith(String suffix) {
        return startsWith(suffix, value.length - suffix.value.length);
    }

    public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int fromIndex) {
        return indexOf(value, 0, value.length,
                str.value, 0, str.value.length, fromIndex);
    }

    static int indexOf(char[] source, int sourceOffset, int sourceCount,
            String target, int fromIndex) {
        return indexOf(source, sourceOffset, sourceCount,
                target.value, 0, target.value.length,
                fromIndex);
    }

    static int indexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(String str) {
        return lastIndexOf(str, value.length);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return lastIndexOf(value, 0, value.length,
                str.value, 0, str.value.length, fromIndex);
    }

    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
            String target, int fromIndex) {
        return lastIndexOf(source, sourceOffset, sourceCount,
                target.value, 0, target.value.length,
                fromIndex);
    }

    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        char strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

   public String substring(int beginIndex) {
       if (beginIndex < 0) {
           throw new ArrayIndexOutOfBoundsException(Integer.toString(beginIndex));
       }
       int subLen = value.length - beginIndex;
       if (subLen < 0) {
           throw new ArrayIndexOutOfBoundsException(Integer.toString(subLen));
       }
       String s = new String();
       for (int i = beginIndex; i < value.length; i++) {
           s += value[i];
       }
       return (beginIndex == 0) ? this : s;
   }

   public String substring(int beginIndex, int endIndex) {
       if (beginIndex < 0) {
           throw new ArrayIndexOutOfBoundsException(Integer.toString(beginIndex));
       }
       if (endIndex > value.length) {
           throw new ArrayIndexOutOfBoundsException(Integer.toString(endIndex));
       }
       int subLen = endIndex - beginIndex;
       if (subLen < 0) {
           throw new ArrayIndexOutOfBoundsException(Integer.toString(subLen));
       }
       String s = new String();
       for (int i = beginIndex; i < value.length && i < endIndex; i++) {
           s += value[i];
       }
       return ((beginIndex == 0) && (endIndex == value.length)) ? this
               : s;
   }

    public String replace(char oldChar, char newChar) {
        if (oldChar != newChar) {
            int len = value.length;
            int i = -1;
            char[] val = value;
            /* avoid getfield opcode */

            while (++i < len) {
                if (val[i] == oldChar) {
                    break;
                }
            }
            if (i < len) {
                char buf[] = new char[len];
                for (int j = 0; j < i; j++) {
                    buf[j] = val[j];
                }
                while (i < len) {
                    char c = val[i];
                    buf[i] = (c == oldChar) ? newChar : c;
                    i++;
                }
                return new String(buf);
            }
        }
        return this;
    }

    public boolean contains(String s) {
        return indexOf(s.toString()) > -1;
    }

//      public String[] split(String splitor) {
//        return split(splitor, 0);
//      }
//
//      public String[] split(String splitor, int limit) {
//        String[] result = new String[0];
//        int startAt = 0;
//        for (int i = 0; i < count;) {
//            char ch = value[offset + i];
//            boolean match = false;
//            if (ch == splitor.charAt(0)) {
//                match = true;
//                for (int j = 1; j < splitor.count; j++) {
//                    if (offset + i + j >= count || value[offset + i + j] != splitor.charAt(j)) {
//                        match = false;
//                        break;
//                    }
//                }
//            }
//            if (match) {
//                result = expandArr(result);
//                result[result.length - 1] = new String(value, startAt + offset, i - startAt);
//                i += splitor.count;
//                startAt = i;
//                if (limit > 0 && result.length >= limit) {
//                    return result;
//                }
//            } else {
//                i++;
//            }
//        }
//        String last = new String(value, startAt + offset, count - startAt);
//        result = expandArr(result);
//        result[result.length - 1] = last;
//        return result;
//      }

   public String trim() {
       int len = value.length;
       int st = 0;
       char[] val = value;
       /* avoid getfield opcode */

       while ((st < len) && (val[st] <= ' ')) {
           st++;
       }
       while ((st < len) && (val[len - 1] <= ' ')) {
           len--;
       }
       return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
   }

    /**
     * This object (which is already a string!) is itself returned.
     *
     * @return the string itself.
     */
    public String toString() {
        return this;
    }

    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    public static String valueOf(char data[]) {
        return new String(data);
    }

    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

   public static String valueOf(char c) {
       char data[] = {c};
       return new String(data);
   }

   public static String valueOf(int i) {
       return Integer.toString(i);
   }

   public static String valueOf(long l) {
       return Long.toString(l);
   }

   public static String valueOf(float f) {
       return Float.toString(f);
   }

   public static String valueOf(double d) {
       return Double.toString(d);
   }
//    public native String intern();
}
