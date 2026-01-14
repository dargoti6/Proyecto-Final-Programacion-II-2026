package negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Aplicacion {

    private final String id = UUID.randomUUID().toString();
    private String nombre;
    private String ip;
    private EstadoAplicacion estado = EstadoAplicacion.ACTIVA;

    private final List<IntentoConexion> historialUso = new ArrayList<>();

    public Aplicacion(String nombre, String ip) {
        this.nombre = nombre;
        this.ip = ip;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getIp() { return ip; }
    public EstadoAplicacion getEstado() { return estado; }
    public List<IntentoConexion> getHistorialUso() { return historialUso; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setIp(String ip) { this.ip = ip; }
    public void setEstado(EstadoAplicacion estado) { this.estado = estado; }

    public void registrarUso(IntentoConexion intento) {
        if (intento != null) historialUso.add(intento);
    }

    @Override
    public String toString() {
        return "Aplicacion{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ip='" + ip + '\'' +
                ", estado=" + estado +
                '}';
    }
}
