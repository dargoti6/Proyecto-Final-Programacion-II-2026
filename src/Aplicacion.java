public class Aplicacion extends EntidadActiva {

    private int idAplicacion;
    private String nombre;
    private String descripcion;
    private String direccionIp;
    private String categoriaMinima; // EJ: "ESTUDIANTE", "DOCENTE", "ADMIN"

    public Aplicacion(int idAplicacion, String nombre, String descripcion,
                      String direccionIp, String categoriaMinima) {
        super(); // EntidadActiva -> activo = true por defecto
        this.idAplicacion = idAplicacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccionIp = direccionIp;
        this.categoriaMinima = categoriaMinima;
    }

    public int getIdAplicacion() {
        return idAplicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getCategoriaMinima() {
        return categoriaMinima;
    }

    public void setCategoriaMinima(String categoriaMinima) {
        this.categoriaMinima = categoriaMinima;
    }

    @Override
    public String toString() {
        return "Aplicacion{" +
                "idAplicacion=" + idAplicacion +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", direccionIp='" + direccionIp + '\'' +
                ", categoriaMinima='" + categoriaMinima + '\'' +
                ", activa=" + isActivo() +
                '}';
    }
}
