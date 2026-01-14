package negocio;

public class CredencialCesar extends Credencial {

    private final String cipherText;
    private final int shift;

    public CredencialCesar(String alias, String usernameAsociado, String secretoPlano, int shift) {
        super(TipoCredencial.CESAR, alias, usernameAsociado);
        this.shift = shift;
        this.cipherText = CesarCipher.encrypt(secretoPlano, shift);
    }

    @Override
    public boolean verificar(String intento) {
        String plain = CesarCipher.decrypt(cipherText, shift);
        return plain.equals(intento);
    }

    public int getShift() { return shift; }
}
