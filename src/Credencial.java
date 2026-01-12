import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Credencial {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int idCredencial;
    private Usuario usuario;
    private Aplicacion aplicacion;
    private String nombreUsuarioAplicacion;
    private String contraseniaProtegida;
    private String tipoProteccion;      // "HASH", "CESAR", "VIGENERE"
    private int desplazamientoCesar;
    private String claveVigenere;
    private String nota;
    private String fechaCreacion;
    private String fechaActualizacion;

    public Credencial(int idCredencial,
                      Usuario usuario,
                      Aplicacion aplicacion,
                      String nombreUsuarioAplicacion,
                      String contraseniaProtegida,
                      String tipoProteccion,
                      int desplazamientoCesar,
                      String claveVigenere,
                      String nota) {

        this.idCredencial = idCredencial;
        this.usuario = usuario;
        this.aplicacion = aplicacion;
        this.nombreUsuarioAplicacion = nombreUsuarioAplicacion;
        this.contraseniaProtegida = contraseniaProtegida;
        this.tipoProteccion = tipoProteccion;
        this.desplazamientoCesar = desplazamientoCesar;
        this.claveVigenere = claveVigenere;
        this.nota = nota;

        String ahora = obtenerFechaActual();
        this.fechaCreacion = ahora;
        this.fechaActualizacion = ahora;
    }

    private String obtenerFechaActual() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }

    public int getIdCredencial() {
        return idCredencial;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Aplicacion getAplicacion() {
        return aplicacion;
    }

    public String getNombreUsuarioAplicacion() {
        return nombreUsuarioAplicacion;
    }

    public void setNombreUsuarioAplicacion(String nombreUsuarioAplicacion) {
        this.nombreUsuarioAplicacion = nombreUsuarioAplicacion;
        actualizarFechaActualizacion();
    }

    public String getContraseniaProtegida() {
        return contraseniaProtegida;
    }

    public void setContraseniaProtegida(String contraseniaProtegida) {
        this.contraseniaProtegida = contraseniaProtegida;
        actualizarFechaActualizacion();
    }

    public String getTipoProteccion() {
        return tipoProteccion;
    }

    public void setTipoProteccion(String tipoProteccion) {
        this.tipoProteccion = tipoProteccion;
        actualizarFechaActualizacion();
    }

    public int getDesplazamientoCesar() {
        return desplazamientoCesar;
    }

    public void setDesplazamientoCesar(int desplazamientoCesar) {
        this.desplazamientoCesar = desplazamientoCesar;
        actualizarFechaActualizacion();
    }

    public String getClaveVigenere() {
        return claveVigenere;
    }

    public void setClaveVigenere(String claveVigenere) {
        this.claveVigenere = claveVigenere;
        actualizarFechaActualizacion();
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
        actualizarFechaActualizacion();
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void actualizarFechaActualizacion() {
        this.fechaActualizacion = obtenerFechaActual();
    }

    @Override
    public String toString() {
        return "Credencial{" +
                "idCredencial=" + idCredencial +
                ", usuario=" + usuario.getCorreo() +
                ", aplicacion=" + aplicacion.getNombre() +
                ", nombreUsuarioAplicacion='" + nombreUsuarioAplicacion + '\'' +
                ", tipoProteccion='" + tipoProteccion + '\'' +
                '}';
    }
}
