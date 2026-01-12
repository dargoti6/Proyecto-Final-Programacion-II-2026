import java.util.ArrayList;
import java.util.List;

public class GestorAplicaciones {

    private List<Aplicacion> listaAplicaciones;
    private int siguienteId;

    public GestorAplicaciones() {
        this.listaAplicaciones = new ArrayList<>();
        this.siguienteId = 1;
    }

    public Aplicacion registrarAplicacion(String nombre, String descripcion,
                                          String direccionIp, String categoriaMinima) {
        Aplicacion nueva = new Aplicacion(siguienteId, nombre, descripcion, direccionIp, categoriaMinima);
        siguienteId++;
        listaAplicaciones.add(nueva);
        return nueva;
    }

    public Aplicacion buscarPorId(int idAplicacion) {
        for (Aplicacion app : listaAplicaciones) {
            if (app.getIdAplicacion() == idAplicacion) {
                return app;
            }
        }
        return null;
    }

    public List<Aplicacion> buscarPorNombre(String texto) {
        List<Aplicacion> resultado = new ArrayList<>();
        String textoLower = texto.toLowerCase();
        for (Aplicacion app : listaAplicaciones) {
            if (app.getNombre().toLowerCase().contains(textoLower)) {
                resultado.add(app);
            }
        }
        return resultado;
    }

    public List<Aplicacion> listarAplicaciones() {
        return new ArrayList<>(listaAplicaciones); // copia defensiva
    }

    public boolean actualizarDireccionIp(int idAplicacion, String nuevaDireccionIp) {
        Aplicacion app = buscarPorId(idAplicacion);
        if (app != null) {
            app.setDireccionIp(nuevaDireccionIp);
            return true;
        }
        return false;
    }

    public boolean actualizarCategoriaMinima(int idAplicacion, String nuevaCategoriaMinima) {
        Aplicacion app = buscarPorId(idAplicacion);
        if (app != null) {
            app.setCategoriaMinima(nuevaCategoriaMinima);
            return true;
        }
        return false;
    }

    public boolean desactivarAplicacion(int idAplicacion) {
        Aplicacion app = buscarPorId(idAplicacion);
        if (app != null) {
            app.desactivar();
            return true;
        }
        return false;
    }

    public boolean activarAplicacion(int idAplicacion) {
        Aplicacion app = buscarPorId(idAplicacion);
        if (app != null) {
            app.activar();
            return true;
        }
        return false;
    }

    public boolean existeAplicacionActiva(int idAplicacion) {
        Aplicacion app = buscarPorId(idAplicacion);
        return app != null && app.isActivo();
    }
}
