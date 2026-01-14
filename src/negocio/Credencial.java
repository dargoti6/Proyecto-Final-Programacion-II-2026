package negocio;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Credencial {

    private final String id = UUID.randomUUID().toString();
    private final LocalDateTime creadaEn = LocalDateTime.now();

    private final TipoCredencial tipo;
    private String alias;
    private String usernameAsociado;

    protected Credencial(TipoCredencial tipo, String alias, String usernameAsociado) {
        this.tipo = tipo;
        this.alias = alias;
        this.usernameAsociado = usernameAsociado;
    }

    public String getId() { return id; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
    public TipoCredencial getTipo() { return tipo; }
    public String getAlias() { return alias; }
    public String getUsernameAsociado() { return usernameAsociado; }

    public void setAlias(String alias) { this.alias = alias; }
    public void setUsernameAsociado(String usernameAsociado) { this.usernameAsociado = usernameAsociado; }

    public abstract boolean verificar(String intento) throws ValidacionException;

    @Override
    public String toString() {
        return "Credencial{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", alias='" + alias + '\'' +
                ", usernameAsociado='" + usernameAsociado + '\'' +
                ", creadaEn=" + creadaEn +
                '}';
    }
}
