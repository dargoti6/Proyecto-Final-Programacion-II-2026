import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {

    private List<Usuario> listaUsuarios;
    private int siguienteId;

    public GestorUsuarios() {
        this.listaUsuarios = new ArrayList<>();
        this.siguienteId = 1;
    }

    public Usuario registrarUsuario(String nombre, String apellido, String correo, String categoria) {
        if (correoYaRegistrado(correo)) {
            return null; // ya existe un usuario con ese correo
        }
        Usuario nuevo = new Usuario(siguienteId, nombre, apellido, correo, categoria);
        siguienteId++;
        listaUsuarios.add(nuevo);
        return nuevo;
    }

    public Usuario buscarPorId(int idUsuario) {
        for (Usuario u : listaUsuarios) {
            if (u.getIdUsuario() == idUsuario) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarPorCorreo(String correo) {
        for (Usuario u : listaUsuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(listaUsuarios); // copia defensiva
    }

    public boolean bloquearUsuario(int idUsuario) {
        Usuario u = buscarPorId(idUsuario);
        if (u != null) {
            u.desactivar();
            return true;
        }
        return false;
    }

    public boolean activarUsuario(int idUsuario) {
        Usuario u = buscarPorId(idUsuario);
        if (u != null) {
            u.activar();
            return true;
        }
        return false;
    }

    public boolean actualizarCategoriaUsuario(int idUsuario, String nuevaCategoria) {
        Usuario u = buscarPorId(idUsuario);
        if (u != null) {
            u.setCategoria(nuevaCategoria);
            return true;
        }
        return false;
    }

    public boolean correoYaRegistrado(String correo) {
        return buscarPorCorreo(correo) != null;
    }
}
