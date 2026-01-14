package negocio;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class Sistema {

    private final Map<String, Usuario> usuariosPorCedula = new HashMap<>();
    private final Map<String, Usuario> usuariosPorCorreo = new HashMap<>();
    private final Map<String, Aplicacion> appsPorNombre = new HashMap<>();

    private final Map<String, Integer> intentosFallidosLogin = new HashMap<>();

    private final List<IntentoConexion> conexionesGlobal = new ArrayList<>();

    private final ParametrosSeguridad parametros = new ParametrosSeguridad();
    private int contadorAccesosIlegitimos = 0;

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Sistema() {
        // Super Admin por defecto
        Usuario admin = new Usuario(
                "Super Admin",
                LocalDate.of(2000, 1, 1),
                "0000000000",
                "admin@system.local",
                "Admin1234"
        );
        admin.setRol(Rol.ADMIN);
        usuariosPorCedula.put(admin.getCedula(), admin);
        usuariosPorCorreo.put(admin.getCorreo().toLowerCase(), admin);
    }

    // -------------------------
    // Ventana 1
    // -------------------------
    public Usuario registrarUsuario(String nombre, LocalDate fdn, String cedula, String correo,
                                    String pass1, String pass2) throws ValidacionException {

        nombre = safe(nombre);
        cedula = safe(cedula);
        correo = safe(correo).toLowerCase();
        pass1 = safe(pass1);
        pass2 = safe(pass2);

        if (nombre.isEmpty()) throw new ValidacionException("El nombre es obligatorio.");
        if (fdn == null) throw new ValidacionException("La fecha de nacimiento es obligatoria.");

        validarCedulaEcuador(cedula);
        validarCorreo(correo);

        if (usuariosPorCedula.containsKey(cedula)) throw new ValidacionException("Ya existe un usuario con esa cédula.");
        if (usuariosPorCorreo.containsKey(correo)) throw new ValidacionException("Ya existe un usuario con ese correo.");

        if (!pass1.equals(pass2)) throw new ValidacionException("Las contraseñas no coinciden.");
        validarPassword(pass1);

        Usuario u = new Usuario(nombre, fdn, cedula, correo, pass1);
        usuariosPorCedula.put(cedula, u);
        usuariosPorCorreo.put(correo, u);
        return u;
    }

    public Usuario iniciarSesion(String identificador, String password)
            throws ValidacionException, EstadoInvalidoException {

        identificador = safe(identificador).toLowerCase();
        password = safe(password);

        if (identificador.isEmpty()) throw new ValidacionException("El identificador (correo o cédula) es obligatorio.");
        if (password.isEmpty()) throw new ValidacionException("La contraseña es obligatoria.");

        Usuario u = buscarUsuarioPorIdentificador(identificador);
        if (u == null) throw new ValidacionException("Credenciales inválidas.");

        if (u.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new EstadoInvalidoException("Tu usuario está bloqueado. Contacta al administrador.");
        }

        boolean ok = u.verificarPassword(password);
        if (!ok) {
            int fallos = intentosFallidosLogin.getOrDefault(u.getCedula(), 0) + 1;
            intentosFallidosLogin.put(u.getCedula(), fallos);

            if (fallos >= parametros.getMaxIntentosFallidosLogin()) {
                u.setEstado(EstadoUsuario.BLOQUEADO);
                throw new EstadoInvalidoException("Usuario bloqueado por demasiados intentos fallidos.");
            }
            throw new ValidacionException("Credenciales inválidas.");
        }

        intentosFallidosLogin.put(u.getCedula(), 0);
        return u;
    }

    private Usuario buscarUsuarioPorIdentificador(String id) {
        if (id == null) return null;
        if (id.contains("@")) return usuariosPorCorreo.get(id.toLowerCase());
        return usuariosPorCedula.get(id);
    }

    // -------------------------
    // 2.1 Gestión Aplicaciones
    // -------------------------
    public Aplicacion registrarAplicacion(Usuario solicitante, String nombre, String ip)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(solicitante);

        nombre = safe(nombre);
        ip = safe(ip);

        if (nombre.isEmpty()) throw new ValidacionException("El nombre de la aplicación es obligatorio.");
        validarIPv4(ip);

        String key = nombre.toLowerCase();
        if (appsPorNombre.containsKey(key)) throw new ValidacionException("Ya existe una aplicación con ese nombre.");

        Aplicacion app = new Aplicacion(nombre, ip);
        appsPorNombre.put(key, app);
        return app;
    }

    public Aplicacion buscarAplicacion(String nombre) throws ValidacionException, NoEncontradoException {
        nombre = safe(nombre);
        if (nombre.isEmpty()) throw new ValidacionException("Debes ingresar el nombre de la aplicación.");
        Aplicacion app = appsPorNombre.get(nombre.toLowerCase());
        if (app == null) throw new NoEncontradoException("Aplicación no encontrada.");
        return app;
    }

    public void actualizarIpAplicacion(Usuario solicitante, String nombreApp, String nuevaIp)
            throws ValidacionException, NoEncontradoException, EstadoInvalidoException {

        requireSesionActiva(solicitante);

        Aplicacion app = buscarAplicacion(nombreApp);
        validarIPv4(nuevaIp);
        app.setIp(nuevaIp);
    }

    public List<IntentoConexion> historialUsoAplicacion(String nombreApp)
            throws ValidacionException, NoEncontradoException {
        Aplicacion app = buscarAplicacion(nombreApp);
        return new ArrayList<>(app.getHistorialUso());
    }

    // -------------------------
    // 2.2 Gestión Credenciales
    // -------------------------
    public Credencial crearCredencial(Usuario u, TipoCredencial tipo, String alias, String usernameAsociado,
                                      String secretoPlano, Integer cesarShift, String vigenereKey)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);

        alias = safe(alias);
        usernameAsociado = safe(usernameAsociado);
        secretoPlano = safe(secretoPlano);

        if (alias.isEmpty()) throw new ValidacionException("El alias/descripcion es obligatorio.");
        if (usernameAsociado.isEmpty()) throw new ValidacionException("El usuario asociado (username/email) es obligatorio.");
        if (secretoPlano.isEmpty()) throw new ValidacionException("El secreto/contraseña de la credencial es obligatorio.");
        if (tipo == null) throw new ValidacionException("Debes seleccionar el tipo de credencial.");

        Credencial c;
        switch (tipo) {
            case HASH:
                c = new CredencialHash(alias, usernameAsociado, secretoPlano);
                break;
            case CESAR:
                if (cesarShift == null) throw new ValidacionException("Debes indicar el desplazamiento (shift) para César.");
                c = new CredencialCesar(alias, usernameAsociado, secretoPlano, cesarShift);
                break;
            case VIGENERE:
                c = new CredencialVigenere(alias, usernameAsociado, secretoPlano, vigenereKey);
                break;
            default:
                throw new ValidacionException("Tipo de credencial no soportado.");
        }

        u.agregarCredencial(c);
        return c;
    }

    public List<Credencial> listarCredenciales(Usuario u) throws EstadoInvalidoException {
        requireSesionActiva(u);
        return new ArrayList<>(u.getCredenciales());
    }

    public boolean verificarCredencial(Usuario u, String credId, String intento)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);
        Credencial c = buscarCredencialUsuario(u, credId);
        return c.verificar(safe(intento));
    }

    public void eliminarCredencial(Usuario u, String credId)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);
        Credencial c = buscarCredencialUsuario(u, credId);
        u.eliminarCredencial(c);
    }

    private Credencial buscarCredencialUsuario(Usuario u, String credId) throws ValidacionException {
        credId = safe(credId);
        if (credId.isEmpty()) throw new ValidacionException("Debes indicar el ID de la credencial.");
        for (Credencial c : u.getCredenciales()) {
            if (c.getId().equals(credId)) return c;
        }
        throw new ValidacionException("Credencial no encontrada para este usuario.");
    }

    // -------------------------
    // 2.3 Conexiones
    // -------------------------
    public IntentoConexion intentarConexion(Usuario u, String nombreApp, String credId, String secretoIntento)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);

        nombreApp = safe(nombreApp);
        credId = safe(credId);
        secretoIntento = safe(secretoIntento);

        Aplicacion app = appsPorNombre.get(nombreApp.toLowerCase());

        ResultadoConexion resultado;
        MotivoConexion motivo;

        if (u.getEstado() == EstadoUsuario.BLOQUEADO) {
            resultado = ResultadoConexion.FALLIDO;
            motivo = MotivoConexion.USUARIO_BLOQUEADO;
        } else if (app == null) {
            resultado = ResultadoConexion.FALLIDO;
            motivo = MotivoConexion.APP_NO_EXISTE;
        } else if (app.getEstado() == EstadoAplicacion.BLOQUEADA) {
            resultado = ResultadoConexion.FALLIDO;
            motivo = MotivoConexion.APP_BLOQUEADA;
        } else {
            Credencial c = null;
            for (Credencial cc : u.getCredenciales()) {
                if (cc.getId().equals(credId)) { c = cc; break; }
            }
            if (c == null) {
                resultado = ResultadoConexion.FALLIDO;
                motivo = MotivoConexion.CREDENCIAL_NO_EXISTE;
            } else {
                boolean ok;
                try {
                    ok = c.verificar(secretoIntento);
                } catch (ValidacionException ve) {
                    ok = false;
                }
                if (ok) {
                    resultado = ResultadoConexion.EXITOSO;
                    motivo = MotivoConexion.OK;
                } else {
                    resultado = ResultadoConexion.FALLIDO;
                    motivo = MotivoConexion.CREDENCIAL_INVALIDA;
                }
            }
        }

        IntentoConexion intento = new IntentoConexion(
                u.getCedula(),
                (app == null ? "-" : app.getId()),
                (app == null ? nombreApp : app.getNombre()),
                resultado,
                motivo
        );

        conexionesGlobal.add(intento);
        u.registrarConexion(intento);
        if (app != null) app.registrarUso(intento);

        // Bandeja 2.4.3: siempre se pregunta si el usuario fue quien intentó
        String msg = "Intento de conexión detectado a [" + (app == null ? nombreApp : app.getNombre()) +
                "]. ¿Fuiste tú?";
        u.agregarNotificacion(new Notificacion(msg, intento.getId()));

        return intento;
    }

    public ParametrosSeguridad verRequisitosConexion() {
        return parametros;
    }

    public List<IntentoConexion> verHistorialConexionesCompleto(Usuario admin) throws AccesoDenegadoException {
        requireAdmin(admin);
        return new ArrayList<>(conexionesGlobal);
    }

    public List<IntentoConexion> verHistorialConexionesPorUsuario(Usuario admin, String cedula)
            throws AccesoDenegadoException, NoEncontradoException {

        requireAdmin(admin);
        Usuario u = usuariosPorCedula.get(safe(cedula));
        if (u == null) throw new NoEncontradoException("Usuario no encontrado.");
        return new ArrayList<>(u.getHistorialConexiones());
    }

    public String estadoConexiones() {
        long ok = conexionesGlobal.stream().filter(c -> c.getResultado() == ResultadoConexion.EXITOSO).count();
        long fail = conexionesGlobal.size() - ok;
        return "Conexiones totales: " + conexionesGlobal.size() + " | Exitosas: " + ok + " | Fallidas: " + fail;
    }

    // -------------------------
    // 2.4 Perfil
    // -------------------------
    public void cambiarPassword(Usuario u, String actual, String nueva, String confirmar)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);

        actual = safe(actual);
        nueva = safe(nueva);
        confirmar = safe(confirmar);

        if (!u.verificarPassword(actual)) throw new ValidacionException("La contraseña actual es incorrecta.");
        if (!nueva.equals(confirmar)) throw new ValidacionException("La nueva contraseña y su verificación no coinciden.");
        validarPassword(nueva);

        u.cambiarPassword(nueva);
    }

    public void editarBiografia(Usuario u, String bio) throws EstadoInvalidoException {
        requireSesionActiva(u);
        u.setBiografia(bio);
    }

    public List<Notificacion> listarNotificaciones(Usuario u, boolean soloPendientes) throws EstadoInvalidoException {
        requireSesionActiva(u);
        if (!soloPendientes) return new ArrayList<>(u.getBandeja());

        List<Notificacion> out = new ArrayList<>();
        for (Notificacion n : u.getBandeja()) {
            if (n.getRespuesta() == RespuestaNotificacion.PENDIENTE) out.add(n);
        }
        return out;
    }

    public void responderNotificacion(Usuario u, String notifId, RespuestaNotificacion respuesta)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);
        notifId = safe(notifId);

        if (notifId.isEmpty()) throw new ValidacionException("Debes indicar la notificación.");
        if (respuesta == null || respuesta == RespuestaNotificacion.PENDIENTE)
            throw new ValidacionException("Debes responder SI o NO.");

        Notificacion n = null;
        for (Notificacion nn : u.getBandeja()) {
            if (nn.getId().equals(notifId)) { n = nn; break; }
        }
        if (n == null) throw new ValidacionException("Notificación no encontrada.");
        if (n.getRespuesta() != RespuestaNotificacion.PENDIENTE)
            throw new ValidacionException("Esa notificación ya fue respondida.");

        n.responder(respuesta);

        if (respuesta == RespuestaNotificacion.NO) {
            contadorAccesosIlegitimos++;
        }
    }

    public void eliminarCuenta(Usuario u, String passwordConfirm)
            throws ValidacionException, EstadoInvalidoException {

        requireSesionActiva(u);
        if (u.getRol() == Rol.ADMIN) throw new ValidacionException("No se puede eliminar la cuenta del super administrador.");
        if (!u.verificarPassword(safe(passwordConfirm))) throw new ValidacionException("Contraseña incorrecta.");

        usuariosPorCedula.remove(u.getCedula());
        usuariosPorCorreo.remove(u.getCorreo().toLowerCase());
    }

    // -------------------------
    // 2.5 Admin
    // -------------------------
    public List<Usuario> listarUsuarios(Usuario admin) throws AccesoDenegadoException {
        requireAdmin(admin);
        return new ArrayList<>(usuariosPorCedula.values());
    }

    public List<Aplicacion> listarAplicaciones(Usuario admin) throws AccesoDenegadoException {
        requireAdmin(admin);
        return new ArrayList<>(appsPorNombre.values());
    }

    public void bloquearUsuario(Usuario admin, String cedula) throws AccesoDenegadoException, NoEncontradoException {
        requireAdmin(admin);
        Usuario u = usuariosPorCedula.get(safe(cedula));
        if (u == null) throw new NoEncontradoException("Usuario no encontrado.");
        if (u.getRol() == Rol.ADMIN) return;
        u.setEstado(EstadoUsuario.BLOQUEADO);
    }

    public void activarUsuario(Usuario admin, String cedula) throws AccesoDenegadoException, NoEncontradoException {
        requireAdmin(admin);
        Usuario u = usuariosPorCedula.get(safe(cedula));
        if (u == null) throw new NoEncontradoException("Usuario no encontrado.");
        u.setEstado(EstadoUsuario.ACTIVO);
    }

    public void bloquearAplicacion(Usuario admin, String nombreApp) throws AccesoDenegadoException, NoEncontradoException {
        requireAdmin(admin);
        Aplicacion a = appsPorNombre.get(safe(nombreApp).toLowerCase());
        if (a == null) throw new NoEncontradoException("Aplicación no encontrada.");
        a.setEstado(EstadoAplicacion.BLOQUEADA);
    }

    public void activarAplicacion(Usuario admin, String nombreApp) throws AccesoDenegadoException, NoEncontradoException {
        requireAdmin(admin);
        Aplicacion a = appsPorNombre.get(safe(nombreApp).toLowerCase());
        if (a == null) throw new NoEncontradoException("Aplicación no encontrada.");
        a.setEstado(EstadoAplicacion.ACTIVA);
    }

    public void actualizarParametrosSeguridad(Usuario admin, ParametrosSeguridad nuevos) throws AccesoDenegadoException, ValidacionException {
        requireAdmin(admin);
        if (nuevos == null) throw new ValidacionException("Parámetros inválidos.");

        if (nuevos.getMinPasswordLength() < 6) throw new ValidacionException("minPasswordLength debe ser >= 6.");
        if (nuevos.getMaxIntentosFallidosLogin() < 1) throw new ValidacionException("maxIntentosFallidosLogin debe ser >= 1.");

        parametros.setMinPasswordLength(nuevos.getMinPasswordLength());
        parametros.setRequireUpper(nuevos.isRequireUpper());
        parametros.setRequireLower(nuevos.isRequireLower());
        parametros.setRequireDigit(nuevos.isRequireDigit());
        parametros.setMaxIntentosFallidosLogin(nuevos.getMaxIntentosFallidosLogin());
    }

    public int getContadorAccesosIlegitimos() {
        return contadorAccesosIlegitimos;
    }

    public String revisionVulnerabilidades(Usuario admin) throws AccesoDenegadoException {
        requireAdmin(admin);
        return "Accesos ilegítimos detectados (respuestas NO): " + contadorAccesosIlegitimos;
    }

    public String generarReporte(Usuario admin) throws AccesoDenegadoException {
        requireAdmin(admin);

        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DEL SISTEMA ===\n");
        sb.append("Usuarios registrados: ").append(usuariosPorCedula.size()).append("\n");
        sb.append("Aplicaciones registradas: ").append(appsPorNombre.size()).append("\n");
        sb.append("Conexiones totales: ").append(conexionesGlobal.size()).append("\n");
        sb.append("Accesos ilegítimos detectados (NO): ").append(contadorAccesosIlegitimos).append("\n\n");

        sb.append("Usuarios (resumen):\n");
        for (Usuario u : usuariosPorCedula.values()) {
            sb.append("- ").append(u.getCedula())
                    .append(" | ").append(u.getCorreo())
                    .append(" | ").append(u.getRol())
                    .append(" | ").append(u.getEstado()).append("\n");
        }

        sb.append("\nAplicaciones:\n");
        for (Aplicacion a : appsPorNombre.values()) {
            sb.append("- ").append(a.getNombre())
                    .append(" | ").append(a.getIp())
                    .append(" | ").append(a.getEstado()).append("\n");
        }

        sb.append("\nUsuarios conectados por aplicación (solo EXITOSOS):\n");
        Map<String, Set<String>> mapa = new HashMap<>();
        for (IntentoConexion ic : conexionesGlobal) {
            if (ic.getResultado() == ResultadoConexion.EXITOSO) {
                mapa.computeIfAbsent(ic.getAppNombre(), k -> new HashSet<>()).add(ic.getCedulaUsuario());
            }
        }
        for (Map.Entry<String, Set<String>> e : mapa.entrySet()) {
            sb.append("- ").append(e.getKey()).append(" => ").append(e.getValue()).append("\n");
        }

        return sb.toString();
    }

    // -------------------------
    // Helpers y validaciones
    // -------------------------
    private void requireSesionActiva(Usuario u) throws EstadoInvalidoException {
        if (u == null) throw new EstadoInvalidoException("No hay sesión activa.");
        if (u.getEstado() == EstadoUsuario.BLOQUEADO) throw new EstadoInvalidoException("Usuario bloqueado.");
    }

    private void requireAdmin(Usuario u) throws AccesoDenegadoException {
        if (u == null || u.getRol() != Rol.ADMIN) throw new AccesoDenegadoException("Acceso denegado: solo administrador.");
    }

    private void validarCorreo(String correo) throws ValidacionException {
        if (correo.isEmpty()) throw new ValidacionException("El correo es obligatorio.");
        if (!EMAIL.matcher(correo).matches()) throw new ValidacionException("Correo inválido.");
    }

    private void validarPassword(String pass) throws ValidacionException {
        if (pass.length() < parametros.getMinPasswordLength())
            throw new ValidacionException("La contraseña debe tener al menos " + parametros.getMinPasswordLength() + " caracteres.");

        if (parametros.isRequireUpper() && pass.chars().noneMatch(Character::isUpperCase))
            throw new ValidacionException("La contraseña debe contener al menos una mayúscula.");
        if (parametros.isRequireLower() && pass.chars().noneMatch(Character::isLowerCase))
            throw new ValidacionException("La contraseña debe contener al menos una minúscula.");
        if (parametros.isRequireDigit() && pass.chars().noneMatch(Character::isDigit))
            throw new ValidacionException("La contraseña debe contener al menos un dígito.");
    }

    private void validarIPv4(String ip) throws ValidacionException {
        if (ip.isEmpty()) throw new ValidacionException("La IP es obligatoria.");
        String[] parts = ip.split("\\.");
        if (parts.length != 4) throw new ValidacionException("IP inválida.");
        for (String p : parts) {
            if (!p.matches("\\d+")) throw new ValidacionException("IP inválida.");
            int v = Integer.parseInt(p);
            if (v < 0 || v > 255) throw new ValidacionException("IP inválida.");
        }
    }

    private void validarCedulaEcuador(String cedula) throws ValidacionException {
        if (cedula.isEmpty()) throw new ValidacionException("La cédula es obligatoria.");
        if (!cedula.matches("\\d{10}")) throw new ValidacionException("La cédula debe tener 10 dígitos.");

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercero = Integer.parseInt(cedula.substring(2, 3));
        if (provincia < 1 || provincia > 24) throw new ValidacionException("Cédula inválida (provincia).");
        if (tercero >= 6) throw new ValidacionException("Cédula inválida (tercer dígito).");

        int[] coef = {2,1,2,1,2,1,2,1,2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int d = Character.getNumericValue(cedula.charAt(i));
            int prod = d * coef[i];
            if (prod >= 10) prod -= 9;
            suma += prod;
        }
        int verificador = (10 - (suma % 10)) % 10;
        int ultimo = Character.getNumericValue(cedula.charAt(9));
        if (verificador != ultimo) throw new ValidacionException("Cédula inválida (verificador).");
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
