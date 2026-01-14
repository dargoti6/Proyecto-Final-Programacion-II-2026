package negocio;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificacion {

    private final String id = UUID.randomUUID().toString();
    private final LocalDateTime fechaHora = LocalDateTime.now();
    private final String mensaje;
    private RespuestaNotificacion respuesta = RespuestaNotificacion.PENDIENTE;

    // enlace lógico (útil para auditoría)
    private final String intentoConexionId;

    public Notificacion(String mensaje, String intentoConexionId) {
        this.mensaje = mensaje;
        this.intentoConexionId = intentoConexionId;
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMensaje() { return mensaje; }
    public RespuestaNotificacion getRespuesta() { return respuesta; }
    public String getIntentoConexionId() { return intentoConexionId; }

    public void responder(RespuestaNotificacion r) {
        if (r == null) return;
        this.respuesta = r;
    }

    @Override
    public String toString() {
        return "[" + fechaHora + "] " + mensaje + " (" + respuesta + ")";
    }
}
