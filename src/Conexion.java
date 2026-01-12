public class Conexion {

    private int idConexion;
    private Usuario usuario;
    private Aplicacion aplicacion;
    private String direccionIpOrigen;
    private String fechaHora;
    private boolean exito;
    private String motivo;

    public Conexion(int idConexion,
                    Usuario usuario,
                    Aplicacion aplicacion,
                    String direccionIpOrigen,
                    String fechaHora,
                    boolean exito,
                    String motivo) {
        this.idConexion = idConexion;
        this.usuario = usuario;
        this.aplicacion = aplicacion;
        this.direccionIpOrigen = direccionIpOrigen;
        this.fechaHora = fechaHora;
        this.exito = exito;
        this.motivo = motivo;
    }

    public int getIdConexion() {
        return idConexion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Aplicacion getAplicacion() {
        return aplicacion;
    }

    public String getDireccionIpOrigen() {
        return direccionIpOrigen;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public boolean isExito() {
        return exito;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return "Conexion{" +
                "idConexion=" + idConexion +
                ", usuario=" + (usuario != null ? usuario.getCorreo() : "null") +
                ", aplicacion=" + (aplicacion != null ? aplicacion.getNombre() : "null") +
                ", direccionIpOrigen='" + direccionIpOrigen + '\'' +
                ", fechaHora='" + fechaHora + '\'' +
                ", exito=" + exito +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
