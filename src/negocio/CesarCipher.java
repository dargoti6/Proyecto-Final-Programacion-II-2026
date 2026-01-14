package negocio;

public final class CesarCipher {

    private static final int MIN = 32;  // espacio
    private static final int MAX = 126; // ~
    private static final int RANGE = (MAX - MIN + 1);

    private CesarCipher() {}

    public static String encrypt(String plain, int shift) {
        if (plain == null) return "";
        int s = mod(shift, RANGE);
        StringBuilder sb = new StringBuilder(plain.length());
        for (char c : plain.toCharArray()) {
            if (c < MIN || c > MAX) { sb.append(c); continue; }
            int x = c - MIN;
            int y = (x + s) % RANGE;
            sb.append((char) (y + MIN));
        }
        return sb.toString();
    }

    public static String decrypt(String cipher, int shift) {
        if (cipher == null) return "";
        return encrypt(cipher, -shift);
    }

    private static int mod(int a, int m) {
        int r = a % m;
        return (r < 0) ? r + m : r;
    }
}
