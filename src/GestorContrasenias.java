import java.util.ArrayList;
import java.util.List;

public class GestorContrasenias {

    private List<Credencial> listaCredenciales;
    private int siguienteId;
    private ModuloHash moduloHash;
    private ModuloCifradoSimple moduloCifradoSimple;

    public GestorContrasenias(ModuloHash moduloHash, ModuloCifradoSimple moduloCifradoSimple) {
        this.listaCredenciales = new ArrayList<>();
        this.siguienteId = 1;
        this.moduloHash = moduloHash;
        this.moduloCifradoSimple = moduloCifradoSimple;
    }

    public Credencial crearCredencialConHash(Usuario usuario,
                                             Aplicacion aplicacion,
                                             String nombreUsuarioAplicacion,
                                             String contraseniaEnClaro,
                                             String nota) {

        String hash = moduloHash.generarHash(contraseniaEnClaro);
        Credencial cred = new Credencial(
                siguienteId,
                usuario,
                aplicacion,
                nombreUsuarioAplicacion,
                hash,
                "HASH",
                0,
                "",
                nota
        );
        siguienteId++;
        listaCredenciales.add(cred);
        return cred;
    }

    public Credencial crearCredencialConCesar(Usuario usuario,
                                              Aplicacion aplicacion,
                                              String nombreUsuarioAplicacion,
                                              String contraseniaEnClaro,
                                              int desplazamiento,
                                              String nota) {

        String cifrada = moduloCifradoSimple.cifrarCesar(contraseniaEnClaro, desplazamiento);
        Credencial cred = new Credencial(
                siguienteId,
                usuario,
                aplicacion,
                nombreUsuarioAplicacion,
                cifrada,
                "CESAR",
                desplazamiento,
                "",
                nota
        );
        siguienteId++;
        listaCredenciales.add(cred);
        return cred;
    }

    public Credencial crearCredencialConVigenere(Usuario usuario,
                                                 Aplicacion aplicacion,
                                                 String nombreUsuarioAplicacion,
                                                 String contraseniaEnClaro,
                                                 String clave,
                                                 String nota) {

        String cifrada = moduloCifradoSimple.cifrarVigenere(contraseniaEnClaro, clave);
        Credencial cred = new Credencial(
                siguienteId,
                usuario,
                aplicacion,
                nombreUsuarioAplicacion,
                cifrada,
                "VIGENERE",
                0,
                clave,
                nota
        );
        siguienteId++;
        listaCredenciales.add(cred);
        return cred;
    }

    public Credencial buscarCredencial(Usuario usuario, Aplicacion aplicacion) {
        for (Credencial c : listaCredenciales) {
            if (c.getUsuario().getIdUsuario() == usuario.getIdUsuario()
                    && c.getAplicacion().getIdAplicacion() == aplicacion.getIdAplicacion()) {
                return c;
            }
        }
        return null;
    }

    public List<Credencial> obtenerCredencialesDeUsuario(Usuario usuario) {
        List<Credencial> resultado = new ArrayList<>();
        for (Credencial c : listaCredenciales) {
            if (c.getUsuario().getIdUsuario() == usuario.getIdUsuario()) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean eliminarCredencial(Usuario usuario, Aplicacion aplicacion) {
        Credencial c = buscarCredencial(usuario, aplicacion);
        if (c != null) {
            return listaCredenciales.remove(c);
        }
        return false;
    }

    public boolean actualizarContraseniaConHash(Usuario usuario,
                                                Aplicacion aplicacion,
                                                String nuevaContraseniaEnClaro) {

        Credencial c = buscarCredencial(usuario, aplicacion);
        if (c == null) {
            return false;
        }
        String nuevoHash = moduloHash.generarHash(nuevaContraseniaEnClaro);
        c.setContraseniaProtegida(nuevoHash);
        c.setTipoProteccion("HASH");
        c.setDesplazamientoCesar(0);
        c.setClaveVigenere("");
        return true;
    }

    public boolean actualizarContraseniaConCesar(Usuario usuario,
                                                 Aplicacion aplicacion,
                                                 String nuevaContraseniaEnClaro,
                                                 int nuevoDesplazamiento) {

        Credencial c = buscarCredencial(usuario, aplicacion);
        if (c == null) {
            return false;
        }
        String cifrada = moduloCifradoSimple.cifrarCesar(nuevaContraseniaEnClaro, nuevoDesplazamiento);
        c.setContraseniaProtegida(cifrada);
        c.setTipoProteccion("CESAR");
        c.setDesplazamientoCesar(nuevoDesplazamiento);
        c.setClaveVigenere("");
        return true;
    }

    public boolean actualizarContraseniaConVigenere(Usuario usuario,
                                                    Aplicacion aplicacion,
                                                    String nuevaContraseniaEnClaro,
                                                    String nuevaClave) {

        Credencial c = buscarCredencial(usuario, aplicacion);
        if (c == null) {
            return false;
        }
        String cifrada = moduloCifradoSimple.cifrarVigenere(nuevaContraseniaEnClaro, nuevaClave);
        c.setContraseniaProtegida(cifrada);
        c.setTipoProteccion("VIGENERE");
        c.setDesplazamientoCesar(0);
        c.setClaveVigenere(nuevaClave);
        return true;
    }

    public boolean verificarContrasenia(Usuario usuario,
                                        Aplicacion aplicacion,
                                        String contraseniaIngresada) {

        Credencial c = buscarCredencial(usuario, aplicacion);
        if (c == null) {
            return false;
        }

        String tipo = c.getTipoProteccion();
        if ("HASH".equalsIgnoreCase(tipo)) {
            return moduloHash.verificarContrasenia(contraseniaIngresada, c.getContraseniaProtegida());
        } else if ("CESAR".equalsIgnoreCase(tipo)) {
            String cifrada = moduloCifradoSimple.cifrarCesar(contraseniaIngresada, c.getDesplazamientoCesar());
            return cifrada.equals(c.getContraseniaProtegida());
        } else if ("VIGENERE".equalsIgnoreCase(tipo)) {
            String cifrada = moduloCifradoSimple.cifrarVigenere(contraseniaIngresada, c.getClaveVigenere());
            return cifrada.equals(c.getContraseniaProtegida());
        }

        return false;
    }

    public String obtenerContraseniaEnClaro(Usuario usuario, Aplicacion aplicacion) {
        Credencial c = buscarCredencial(usuario, aplicacion);
        if (c == null) {
            return null;
        }

        String tipo = c.getTipoProteccion();
        if ("CESAR".equalsIgnoreCase(tipo)) {
            return moduloCifradoSimple.descifrarCesar(
                    c.getContraseniaProtegida(),
                    c.getDesplazamientoCesar()
            );
        } else if ("VIGENERE".equalsIgnoreCase(tipo)) {
            return moduloCifradoSimple.descifrarVigenere(
                    c.getContraseniaProtegida(),
                    c.getClaveVigenere()
            );
        } else if ("HASH".equalsIgnoreCase(tipo)) {
            return "No disponible para contraseñas con hash";
        }

        return null;
    }
}
