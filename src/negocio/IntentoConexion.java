package negocio;

import java.time.LocalDateTime;
import java.util.UUID;

public class IntentoConexion {

    private final String id = UUID.randomUUID().toString();
    private final LocalDateTime fechaHora = LocalDateTime.now();

    private final String cedulaUsuario;
    private final String appId;
    private final String appNombre;

    private final ResultadoConexion resultado;
    private final MotivoConexion motivo;

    public IntentoConexion(String cedulaUsuario, String appId, String appNombre,
                           ResultadoConexion resultado, MotivoConexion motivo) {
        this.cedulaUsuario = cedulaUsuario;
        this.appId = appId;
        this.appNombre = appNombre;
        this.resultado = resultado;
        this.motivo = motivo;
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getCedulaUsuario() { return cedulaUsuario; }
    public String getAppId() { return appId; }
    public String getAppNombre() { return appNombre; }
    public ResultadoConexion getResultado() { return resultado; }
    public MotivoConexion getMotivo() { return motivo; }

    @Override
    public String toString() {
        return "[" + fechaHora + "] Usuario=" + cedulaUsuario +
                ", App=" + appNombre + ", Resultado=" + resultado +
                ", Motivo=" + motivo;
    }
}
