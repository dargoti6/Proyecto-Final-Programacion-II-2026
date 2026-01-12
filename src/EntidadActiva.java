public class EntidadActiva {

    private boolean activo;

    public EntidadActiva() {
        this.activo = true; // por defecto una entidad está activa
    }

    public boolean isActivo() {
        return activo;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }
}
