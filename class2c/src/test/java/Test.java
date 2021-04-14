public class Test {

    static char[] DIGI = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    static private int indexOfDigi(char ch) {
        ch = Character.toUpperCase(ch);
        for (int i = 0, imax = DIGI.length; i < imax; i++) {
            if (DIGI[i] == ch) {
                return i;
            }
        }
        return -1;
    }

    public static long parseLong(String s, int radix) {
        char[] chars = s.toCharArray();
        long v = 0;
        boolean neg = false;
        int idx = 0;
        if (s.length() > 0) {
            if (chars[idx] == '-') {
                neg = true;
                idx++;
            }
        }
        for (int i = idx, imax = s.length(); i < imax; i++) {
            int ci = indexOfDigi(chars[i]);
            if (ci >= 0 && ci < radix) {
                v = v * radix + ci;
            } else {
                throw new IllegalArgumentException();
            }
        }
        return neg ? -v : v;
    }

    public static String toHexString(long v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int idx = (int) (v >>> ((16 - 1 - i) * 4)) & 0xf;
            if (idx == 0) continue;
            else sb.append(DIGI[idx]);
        }
        return sb.toString();
    }

    public static String toHexString(int v) {
        return Long.toHexString(((long) v) & 0xffffffffL);
    }

    static public void main(String[] args) {

        System.out.println(parseLong("1C6BF526353D4", 16));

        int r = 16;
        long f1 = 0xffffffffffffffffL;
        int f2 = 0xf1234567;
//        String s = Long.toString(f1, r);
        String s = toHexString(f2);
        System.out.println("hex:" + s);
        long v1 = parseLong(s, r);
        System.out.println("hex to long :" + v1);
    }


}

enum TColor {
    RED, GREEN, BLUE;
}