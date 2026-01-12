import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorConexiones {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Conexion> historialConexiones;
    private int siguienteIdConexion;
    private GestorUsuarios gestorUsuarios;
    private GestorAplicaciones gestorAplicaciones;
    private GestorContrasenias gestorContrasenias;
    private GestorSeguridad gestorSeguridad;

    public GestorConexiones(GestorUsuarios gestorUsuarios,
                            GestorAplicaciones gestorAplicaciones,
                            GestorContrasenias gestorContrasenias,
                            GestorSeguridad gestorSeguridad) {

        this.historialConexiones = new ArrayList<>();
        this.siguienteIdConexion = 1;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorAplicaciones = gestorAplicaciones;
        this.gestorContrasenias = gestorContrasenias;
        this.gestorSeguridad = gestorSeguridad;
    }

    private String obtenerFechaActual() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }

    private Aplicacion buscarAplicacionPorNombre(String nombreAplicacion) {
        List<Aplicacion> todas = gestorAplicaciones.listarAplicaciones();
        for (Aplicacion app : todas) {
            if (app.getNombre().equalsIgnoreCase(nombreAplicacion)) {
                return app;
            }
        }
        return null;
    }

    private int nivelCategoria(String categoria) {
        if (categoria == null) return 0;
        String c = categoria.toUpperCase();
        switch (c) {
            case "ESTUDIANTE":
                return 1;
            case "DOCENTE":
                return 2;
            case "ADMIN":
                return 3;
            default:
                return 0;
        }
    }

    public Conexion intentarConexion(String correoUsuario,
                                     String nombreAplicacion,
                                     String contraseniaIngresada,
                                     String direccionIpOrigen) {

        String fechaHora = obtenerFechaActual();

        // 1) Usuario
        Usuario usuario = gestorUsuarios.buscarPorCorreo(correoUsuario);
        if (usuario == null) {
            Conexion con = new Conexion(
                    siguienteIdConexion++, null, null,
                    direccionIpOrigen, fechaHora,
                    false, "USUARIO_NO_ENCONTRADO"
            );
            historialConexiones.add(con);
            return con;
        }

        // 2) Correo válido según dominio
        if (!gestorSeguridad.correoValido(usuario.getCorreo())) {
            gestorSeguridad.registrarIntentoFallido(usuario);
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, null,
                    direccionIpOrigen, fechaHora,
                    false, "CORREO_NO_VALIDO_DOMINIO"
            );
            historialConexiones.add(con);
            return con;
        }

        // 3) Usuario activo
        if (!usuario.isActivo()) {
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, null,
                    direccionIpOrigen, fechaHora,
                    false, "USUARIO_BLOQUEADO"
            );
            historialConexiones.add(con);
            return con;
        }

        // 4) Aplicación
        Aplicacion aplicacion = buscarAplicacionPorNombre(nombreAplicacion);
        if (aplicacion == null) {
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, null,
                    direccionIpOrigen, fechaHora,
                    false, "APLICACION_NO_ENCONTRADA"
            );
            historialConexiones.add(con);
            return con;
        }

        // 5) Aplicación activa
        if (!aplicacion.isActivo()) {
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, aplicacion,
                    direccionIpOrigen, fechaHora,
                    false, "APLICACION_INACTIVA"
            );
            historialConexiones.add(con);
            return con;
        }

        // 6) IP permitida
        if (!gestorSeguridad.ipPermitida(direccionIpOrigen)) {
            gestorSeguridad.registrarIntentoFallido(usuario);
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, aplicacion,
                    direccionIpOrigen, fechaHora,
                    false, "IP_NO_PERMITIDA"
            );
            historialConexiones.add(con);
            return con;
        }

        // 7) Categoria suficiente
        int nivelUsuario = nivelCategoria(usuario.getCategoria());
        int nivelMinimo = nivelCategoria(aplicacion.getCategoriaMinima());
        if (nivelUsuario < nivelMinimo) {
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, aplicacion,
                    direccionIpOrigen, fechaHora,
                    false, "CATEGORIA_INSUFICIENTE"
            );
            historialConexiones.add(con);
            return con;
        }

        // 8) Credencial
        Credencial cred = gestorContrasenias.buscarCredencial(usuario, aplicacion);
        if (cred == null) {
            gestorSeguridad.registrarIntentoFallido(usuario);
            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, aplicacion,
                    direccionIpOrigen, fechaHora,
                    false, "CREDENCIAL_NO_REGISTRADA"
            );
            historialConexiones.add(con);
            return con;
        }

        // 9) Verificar contraseña
        boolean ok = gestorContrasenias.verificarContrasenia(usuario, aplicacion, contraseniaIngresada);
        if (!ok) {
            gestorSeguridad.registrarIntentoFallido(usuario);

            if (gestorSeguridad.debeBloquearUsuario(usuario)) {
                gestorUsuarios.bloquearUsuario(usuario.getIdUsuario());
            }

            Conexion con = new Conexion(
                    siguienteIdConexion++, usuario, aplicacion,
                    direccionIpOrigen, fechaHora,
                    false, "CONTRASENIA_INCORRECTA"
            );
            historialConexiones.add(con);
            return con;
        }

        // 10) Éxito: limpiar intentos fallidos
        gestorSeguridad.limpiarIntentosFallidos(usuario);
        Conexion con = new Conexion(
                siguienteIdConexion++, usuario, aplicacion,
                direccionIpOrigen, fechaHora,
                true, "OK"
        );
        historialConexiones.add(con);
        return con;
    }

    public List<Conexion> obtenerHistorialCompleto() {
        return new ArrayList<>(historialConexiones);
    }

    public List<Conexion> obtenerHistorialPorUsuario(Usuario usuario) {
        List<Conexion> resultado = new ArrayList<>();
        if (usuario == null) {
            return resultado;
        }
        int id = usuario.getIdUsuario();
        for (Conexion c : historialConexiones) {
            if (c.getUsuario() != null && c.getUsuario().getIdUsuario() == id) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<Conexion> obtenerHistorialPorAplicacion(Aplicacion aplicacion) {
        List<Conexion> resultado = new ArrayList<>();
        if (aplicacion == null) {
            return resultado;
        }
        int id = aplicacion.getIdAplicacion();
        for (Conexion c : historialConexiones) {
            if (c.getAplicacion() != null && c.getAplicacion().getIdAplicacion() == id) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<Conexion> obtenerConexionesFallidas() {
        List<Conexion> resultado = new ArrayList<>();
        for (Conexion c : historialConexiones) {
            if (!c.isExito()) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}
