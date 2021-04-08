public class Test {

    static char[] DIGI = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    static private int indexOfDigi(char ch) {
        for (int i = 0, imax = DIGI.length; i < imax; i++) {
            if (DIGI[i] == ch) {
                return i;
            }
        }
        return -1;
    }

    public static long parseLong(String s, int radix) {
        char[] chars=s.toCharArray();
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

    static public void main(String[] args) {

        System.out.println(parseLong("1C6BF526353D4",16));
    }
}
