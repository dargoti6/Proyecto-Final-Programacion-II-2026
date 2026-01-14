package negocio;

public final class VigenereCipher {

    private static final int MIN = 32;
    private static final int MAX = 126;
    private static final int RANGE = (MAX - MIN + 1);

    private VigenereCipher() {}

    public static String encrypt(String plain, String key) throws ValidacionException {
        if (plain == null) return "";
        String k = normalizeKey(key);
        StringBuilder sb = new StringBuilder(plain.length());

        int ki = 0;
        for (char c : plain.toCharArray()) {
            if (c < MIN || c > MAX) { sb.append(c); continue; }
            int shift = k.charAt(ki % k.length()) - MIN;
            int x = c - MIN;
            int y = (x + shift) % RANGE;
            sb.append((char) (y + MIN));
            ki++;
        }
        return sb.toString();
    }

    public static String decrypt(String cipher, String key) throws ValidacionException {
        if (cipher == null) return "";
        String k = normalizeKey(key);
        StringBuilder sb = new StringBuilder(cipher.length());

        int ki = 0;
        for (char c : cipher.toCharArray()) {
            if (c < MIN || c > MAX) { sb.append(c); continue; }
            int shift = k.charAt(ki % k.length()) - MIN;
            int x = c - MIN;
            int y = (x - shift) % RANGE;
            if (y < 0) y += RANGE;
            sb.append((char) (y + MIN));
            ki++;
        }
        return sb.toString();
    }

    private static String normalizeKey(String key) throws ValidacionException {
        if (key == null || key.trim().isEmpty()) throw new ValidacionException("La clave Vigenere no puede estar vacía.");
        String k = key.trim();
        for (char c : k.toCharArray()) {
            if (c < MIN || c > MAX) throw new ValidacionException("La clave Vigenere debe usar caracteres imprimibles ASCII (32-126).");
        }
        return k;
    }
}
