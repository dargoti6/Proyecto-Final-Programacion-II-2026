package negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String nombre;
    private LocalDate fechaNacimiento;
    private String cedula;
    private String correo;

    private String passwordHash; // salt:hash
    private String biografia = "";

    private Rol rol = Rol.USUARIO;
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    private final List<Credencial> credenciales = new ArrayList<>();
    private final List<Notificacion> bandeja = new ArrayList<>();
    private final List<IntentoConexion> historialConexiones = new ArrayList<>();

    public Usuario(String nombre, LocalDate fechaNacimiento, String cedula, String correo, String passwordPlain) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.cedula = cedula;
        this.correo = correo;
        this.passwordHash = Hashing.hashPassword(passwordPlain);
    }

    public String getNombre() { return nombre; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getCedula() { return cedula; }
    public String getCorreo() { return correo; }
    public String getBiografia() { return biografia; }
    public Rol getRol() { return rol; }
    public EstadoUsuario getEstado() { return estado; }

    public List<Credencial> getCredenciales() { return credenciales; }
    public List<Notificacion> getBandeja() { return bandeja; }
    public List<IntentoConexion> getHistorialConexiones() { return historialConexiones; }

    public void setBiografia(String biografia) { this.biografia = biografia == null ? "" : biografia; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    public boolean verificarPassword(String plain) {
        return Hashing.verifyPassword(plain, passwordHash);
    }

    public void cambiarPassword(String nuevoPlain) {
        this.passwordHash = Hashing.hashPassword(nuevoPlain);
    }

    public void agregarCredencial(Credencial c) { if (c != null) credenciales.add(c); }
    public void eliminarCredencial(Credencial c) { if (c != null) credenciales.remove(c); }

    public void agregarNotificacion(Notificacion n) { if (n != null) bandeja.add(n); }
    public void registrarConexion(IntentoConexion ic) { if (ic != null) historialConexiones.add(ic); }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", cedula='" + cedula + '\'' +
                ", correo='" + correo + '\'' +
                ", rol=" + rol +
                ", estado=" + estado +
                '}';
    }
}
