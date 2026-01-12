import java.util.HashMap;
import java.util.Map;

public class GestorSeguridad {

    private String dominioCorreoPermitido;
    private String prefijoIpPermitido;
    private int maxIntentosFallidos;
    private Map<Integer, Integer> intentosFallidosPorUsuario;

    public GestorSeguridad(String dominioCorreoPermitido,
                           String prefijoIpPermitido,
                           int maxIntentosFallidos) {
        this.dominioCorreoPermitido = dominioCorreoPermitido;
        this.prefijoIpPermitido = prefijoIpPermitido;
        this.maxIntentosFallidos = maxIntentosFallidos;
        this.intentosFallidosPorUsuario = new HashMap<>();
    }

    public boolean correoValido(String correo) {
        if (correo == null) {
            return false;
        }
        if (dominioCorreoPermitido == null || dominioCorreoPermitido.isEmpty()) {
            return true; // sin regla de dominio, se acepta cualquiera
        }
        String correoLower = correo.toLowerCase();
        String dominioLower = dominioCorreoPermitido.toLowerCase();
        return correoLower.endsWith(dominioLower);
    }

    public boolean ipPermitida(String direccionIp) {
        if (direccionIp == null) {
            return false;
        }
        if (prefijoIpPermitido == null || prefijoIpPermitido.isEmpty()) {
            return true; // sin regla de IP, se acepta cualquiera
        }
        return direccionIp.startsWith(prefijoIpPermitido);
    }

    public void registrarIntentoFallido(Usuario usuario) {
        if (usuario == null) {
            return;
        }
        int id = usuario.getIdUsuario();
        int actuales = intentosFallidosPorUsuario.getOrDefault(id, 0);
        intentosFallidosPorUsuario.put(id, actuales + 1);
    }

    public void limpiarIntentosFallidos(Usuario usuario) {
        if (usuario == null) {
            return;
        }
        int id = usuario.getIdUsuario();
        intentosFallidosPorUsuario.remove(id);
    }

    public int obtenerIntentosFallidos(int idUsuario) {
        return intentosFallidosPorUsuario.getOrDefault(idUsuario, 0);
    }

    public boolean debeBloquearUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        int id = usuario.getIdUsuario();
        int intentos = obtenerIntentosFallidos(id);
        return intentos >= maxIntentosFallidos;
    }

    public String getDominioCorreoPermitido() {
        return dominioCorreoPermitido;
    }

    public void setDominioCorreoPermitido(String dominioCorreoPermitido) {
        this.dominioCorreoPermitido = dominioCorreoPermitido;
    }

    public String getPrefijoIpPermitido() {
        return prefijoIpPermitido;
    }

    public void setPrefijoIpPermitido(String prefijoIpPermitido) {
        this.prefijoIpPermitido = prefijoIpPermitido;
    }

    public int getMaxIntentosFallidos() {
        return maxIntentosFallidos;
    }

    public void setMaxIntentosFallidos(int maxIntentosFallidos) {
        this.maxIntentosFallidos = maxIntentosFallidos;
    }
}
