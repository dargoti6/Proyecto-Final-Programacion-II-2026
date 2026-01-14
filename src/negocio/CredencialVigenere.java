package negocio;

public class CredencialVigenere extends Credencial {

    private final String cipherText;
    private final String key;

    public CredencialVigenere(String alias, String usernameAsociado, String secretoPlano, String key) throws ValidacionException {
        super(TipoCredencial.VIGENERE, alias, usernameAsociado);
        this.key = key;
        this.cipherText = VigenereCipher.encrypt(secretoPlano, key);
    }

    @Override
    public boolean verificar(String intento) throws ValidacionException {
        String plain = VigenereCipher.decrypt(cipherText, key);
        return plain.equals(intento);
    }

    public String getKey() { return key; }
}
