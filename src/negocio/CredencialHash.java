package negocio;

public class CredencialHash extends Credencial {

    private final String storedHash; // salt:hash

    public CredencialHash(String alias, String usernameAsociado, String secretoPlano) {
        super(TipoCredencial.HASH, alias, usernameAsociado);
        this.storedHash = Hashing.hashPassword(secretoPlano);
    }

    @Override
    public boolean verificar(String intento) {
        return Hashing.verifyPassword(intento, storedHash);
    }
}
