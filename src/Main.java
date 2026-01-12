import java.util.List;
import java.util.Scanner;

public class Main {

    private GestorUsuarios gestorUsuarios;
    private GestorAplicaciones gestorAplicaciones;
    private ModuloHash moduloHash;
    private ModuloCifradoSimple moduloCifradoSimple;
    private GestorContrasenias gestorContrasenias;
    private GestorSeguridad gestorSeguridad;
    private GestorConexiones gestorConexiones;
    private Scanner scanner;

    public static void main(String[] args) {
        Main app = new Main();
        app.mostrarMenuPrincipal();
    }

    public Main() {
        scanner = new Scanner(System.in);
        inicializarSistema();
    }

    private void inicializarSistema() {
        gestorUsuarios = new GestorUsuarios();
        gestorAplicaciones = new GestorAplicaciones();
        moduloHash = new ModuloHash();
        moduloCifradoSimple = new ModuloCifradoSimple();
        gestorContrasenias = new GestorContrasenias(moduloHash, moduloCifradoSimple);

        // Valores por defecto (luego se pueden cambiar desde el menú de seguridad)
        String dominioPorDefecto = "@institucion.edu";
        String prefijoIpPorDefecto = "192.168.1.";
        int maxIntentosFallidos = 3;

        gestorSeguridad = new GestorSeguridad(dominioPorDefecto, prefijoIpPorDefecto, maxIntentosFallidos);
        gestorConexiones = new GestorConexiones(gestorUsuarios, gestorAplicaciones, gestorContrasenias, gestorSeguridad);
    }

    private void mostrarMenuPrincipal() {
        int opcion;
        do {
            System.out.println("\n===== SISTEMA DE GESTIÓN DE ACCESOS =====");
            System.out.println("1. Gestión de usuarios");
            System.out.println("2. Gestión de aplicaciones");
            System.out.println("3. Gestor de credenciales (contraseñas)");
            System.out.println("4. Seguridad y conexiones");
            System.out.println("0. Salir");
            opcion = leerEntero("Seleccione una opción: ");
            manejarOpcion(opcion);
        } while (opcion != 0);

        System.out.println("Saliendo del sistema. ¡Hasta luego!");
    }

    private void manejarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                menuUsuarios();
                break;
            case 2:
                menuAplicaciones();
                break;
            case 3:
                menuCredenciales();
                break;
            case 4:
                menuConexionesSeguridad();
                break;
            case 0:
                // salir
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    // ============================
    // MENÚ USUARIOS
    // ============================
    private void menuUsuarios() {
        int opcion;
        do {
            System.out.println("\n--- MÓDULO 1: GESTIÓN DE USUARIOS ---");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Bloquear usuario");
            System.out.println("4. Activar usuario");
            System.out.println("5. Actualizar categoría de usuario");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    listarUsuarios();
                    break;
                case 3:
                    bloquearUsuario();
                    break;
                case 4:
                    activarUsuario();
                    break;
                case 5:
                    actualizarCategoriaUsuario();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            if (opcion != 0) {
                pausar();
            }
        } while (opcion != 0);
    }

    private void registrarUsuario() {
        System.out.println("\n> Registrar nuevo usuario");
        String nombre = leerLinea("Nombre: ");
        String apellido = leerLinea("Apellido: ");
        String correo = leerLinea("Correo: ");
        String categoria = leerLinea("Categoría (ESTUDIANTE/DOCENTE/ADMIN u otra): ");

        Usuario u = gestorUsuarios.registrarUsuario(nombre, apellido, correo, categoria);
        if (u == null) {
            System.out.println("No se pudo registrar: ya existe un usuario con ese correo.");
        } else {
            System.out.println("Usuario registrado: " + u);
        }
    }

    private void listarUsuarios() {
        System.out.println("\n> Listado de usuarios");
        List<Usuario> usuarios = gestorUsuarios.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : usuarios) {
            System.out.println(u);
        }
    }

    private void bloquearUsuario() {
        System.out.println("\n> Bloquear usuario");
        int id = leerEntero("Ingrese el ID del usuario: ");
        boolean ok = gestorUsuarios.bloquearUsuario(id);
        if (ok) {
            System.out.println("Usuario bloqueado.");
        } else {
            System.out.println("No se encontró un usuario con ese ID.");
        }
    }

    private void activarUsuario() {
        System.out.println("\n> Activar usuario");
        int id = leerEntero("Ingrese el ID del usuario: ");
        boolean ok = gestorUsuarios.activarUsuario(id);
        if (ok) {
            System.out.println("Usuario activado.");
        } else {
            System.out.println("No se encontró un usuario con ese ID.");
        }
    }

    private void actualizarCategoriaUsuario() {
        System.out.println("\n> Actualizar categoría de usuario");
        int id = leerEntero("Ingrese el ID del usuario: ");
        String nuevaCat = leerLinea("Nueva categoría: ");
        boolean ok = gestorUsuarios.actualizarCategoriaUsuario(id, nuevaCat);
        if (ok) {
            System.out.println("Categoría actualizada.");
        } else {
            System.out.println("No se encontró un usuario con ese ID.");
        }
    }

    // ============================
    // MENÚ APLICACIONES
    // ============================
    private void menuAplicaciones() {
        int opcion;
        do {
            System.out.println("\n--- MÓDULO 2: GESTIÓN DE APLICACIONES ---");
            System.out.println("1. Registrar aplicación");
            System.out.println("2. Listar aplicaciones");
            System.out.println("3. Actualizar dirección IP");
            System.out.println("4. Actualizar categoría mínima");
            System.out.println("5. Activar aplicación");
            System.out.println("6. Desactivar aplicación");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    registrarAplicacion();
                    break;
                case 2:
                    listarAplicaciones();
                    break;
                case 3:
                    actualizarDireccionIp();
                    break;
                case 4:
                    actualizarCategoriaMinima();
                    break;
                case 5:
                    activarAplicacion();
                    break;
                case 6:
                    desactivarAplicacion();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            if (opcion != 0) {
                pausar();
            }
        } while (opcion != 0);
    }

    private void registrarAplicacion() {
        System.out.println("\n> Registrar nueva aplicación");
        String nombre = leerLinea("Nombre: ");
        String descripcion = leerLinea("Descripción: ");
        String ip = leerLinea("Dirección IP asociada: ");
        String catMin = leerLinea("Categoría mínima requerida: ");

        Aplicacion app = gestorAplicaciones.registrarAplicacion(nombre, descripcion, ip, catMin);
        System.out.println("Aplicación registrada: " + app);
    }

    private void listarAplicaciones() {
        System.out.println("\n> Listado de aplicaciones");
        List<Aplicacion> apps = gestorAplicaciones.listarAplicaciones();
        if (apps.isEmpty()) {
            System.out.println("No hay aplicaciones registradas.");
            return;
        }
        for (Aplicacion a : apps) {
            System.out.println(a);
        }
    }

    private void actualizarDireccionIp() {
        System.out.println("\n> Actualizar dirección IP de una aplicación");
        listarAplicaciones();
        int id = leerEntero("Ingrese el ID de la aplicación: ");
        String nuevaIp = leerLinea("Nueva dirección IP: ");
        boolean ok = gestorAplicaciones.actualizarDireccionIp(id, nuevaIp);
        if (ok) {
            System.out.println("Dirección IP actualizada.");
        } else {
            System.out.println("No se encontró una aplicación con ese ID.");
        }
    }

    private void actualizarCategoriaMinima() {
        System.out.println("\n> Actualizar categoría mínima de una aplicación");
        listarAplicaciones();
        int id = leerEntero("Ingrese el ID de la aplicación: ");
        String nuevaCat = leerLinea("Nueva categoría mínima: ");
        boolean ok = gestorAplicaciones.actualizarCategoriaMinima(id, nuevaCat);
        if (ok) {
            System.out.println("Categoría mínima actualizada.");
        } else {
            System.out.println("No se encontró una aplicación con ese ID.");
        }
    }

    private void activarAplicacion() {
        System.out.println("\n> Activar aplicación");
        listarAplicaciones();
        int id = leerEntero("Ingrese el ID de la aplicación: ");
        boolean ok = gestorAplicaciones.activarAplicacion(id);
        if (ok) {
            System.out.println("Aplicación activada.");
        } else {
            System.out.println("No se encontró una aplicación con ese ID.");
        }
    }

    private void desactivarAplicacion() {
        System.out.println("\n> Desactivar aplicación");
        listarAplicaciones();
        int id = leerEntero("Ingrese el ID de la aplicación: ");
        boolean ok = gestorAplicaciones.desactivarAplicacion(id);
        if (ok) {
            System.out.println("Aplicación desactivada.");
        } else {
            System.out.println("No se encontró una aplicación con ese ID.");
        }
    }

    // ============================
    // MENÚ CREDENCIALES
    // ============================
    private void menuCredenciales() {
        int opcion;
        do {
            System.out.println("\n--- MÓDULO 3: GESTOR DE CREDENCIALES ---");
            System.out.println("1. Crear credencial con HASH");
            System.out.println("2. Crear credencial con CIFRADO CÉSAR");
            System.out.println("3. Crear credencial con CIFRADO VIGENERE");
            System.out.println("4. Listar credenciales de un usuario");
            System.out.println("5. Eliminar credencial (usuario + aplicación)");
            System.out.println("6. Verificar contraseña (usuario + aplicación)");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    crearCredencialHash();
                    break;
                case 2:
                    crearCredencialCesar();
                    break;
                case 3:
                    crearCredencialVigenere();
                    break;
                case 4:
                    listarCredencialesDeUsuario();
                    break;
                case 5:
                    eliminarCredencial();
                    break;
                case 6:
                    verificarContrasenia();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            if (opcion != 0) {
                pausar();
            }
        } while (opcion != 0);
    }

    private Usuario seleccionarUsuarioPorCorreo() {
        listarUsuarios();
        String correo = leerLinea("Correo del usuario: ");
        Usuario u = gestorUsuarios.buscarPorCorreo(correo);
        if (u == null) {
            System.out.println("Usuario no encontrado.");
        }
        return u;
    }

    private Aplicacion seleccionarAplicacionPorId() {
        listarAplicaciones();
        int idApp = leerEntero("ID de la aplicación: ");
        Aplicacion app = gestorAplicaciones.buscarPorId(idApp);
        if (app == null) {
            System.out.println("Aplicación no encontrada.");
        }
        return app;
    }

    private void crearCredencialHash() {
        System.out.println("\n> Crear credencial con HASH");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;
        Aplicacion app = seleccionarAplicacionPorId();
        if (app == null) return;

        String nombreUsuarioApp = leerLinea("Nombre de usuario en la aplicación: ");
        String contrasenia = leerLinea("Contraseña en claro: ");
        String nota = leerLinea("Nota (opcional): ");

        Credencial c = gestorContrasenias.crearCredencialConHash(u, app, nombreUsuarioApp, contrasenia, nota);
        System.out.println("Credencial creada: " + c);
    }

    private void crearCredencialCesar() {
        System.out.println("\n> Crear credencial con CIFRADO CÉSAR");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;
        Aplicacion app = seleccionarAplicacionPorId();
        if (app == null) return;

        String nombreUsuarioApp = leerLinea("Nombre de usuario en la aplicación: ");
        String contrasenia = leerLinea("Contraseña en claro: ");
        int desplazamiento = leerEntero("Desplazamiento para el cifrado César: ");
        String nota = leerLinea("Nota (opcional): ");

        Credencial c = gestorContrasenias.crearCredencialConCesar(u, app, nombreUsuarioApp, contrasenia, desplazamiento, nota);
        System.out.println("Credencial creada: " + c);
    }

    private void crearCredencialVigenere() {
        System.out.println("\n> Crear credencial con CIFRADO VIGENERE");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;
        Aplicacion app = seleccionarAplicacionPorId();
        if (app == null) return;

        String nombreUsuarioApp = leerLinea("Nombre de usuario en la aplicación: ");
        String contrasenia = leerLinea("Contraseña en claro: ");
        String clave = leerLinea("Clave Vigenere: ");
        String nota = leerLinea("Nota (opcional): ");

        Credencial c = gestorContrasenias.crearCredencialConVigenere(u, app, nombreUsuarioApp, contrasenia, clave, nota);
        System.out.println("Credencial creada: " + c);
    }

    private void listarCredencialesDeUsuario() {
        System.out.println("\n> Listar credenciales de un usuario");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;

        List<Credencial> credenciales = gestorContrasenias.obtenerCredencialesDeUsuario(u);
        if (credenciales.isEmpty()) {
            System.out.println("El usuario no tiene credenciales registradas.");
            return;
        }
        for (Credencial c : credenciales) {
            System.out.println(c);
        }
    }

    private void eliminarCredencial() {
        System.out.println("\n> Eliminar credencial (usuario + aplicación)");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;
        Aplicacion app = seleccionarAplicacionPorId();
        if (app == null) return;

        boolean ok = gestorContrasenias.eliminarCredencial(u, app);
        if (ok) {
            System.out.println("Credencial eliminada.");
        } else {
            System.out.println("No se encontró una credencial para ese usuario y aplicación.");
        }
    }

    private void verificarContrasenia() {
        System.out.println("\n> Verificar contraseña (usuario + aplicación)");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;
        Aplicacion app = seleccionarAplicacionPorId();
        if (app == null) return;

        String contrasenia = leerLinea("Ingrese la contraseña a verificar: ");
        boolean ok = gestorContrasenias.verificarContrasenia(u, app, contrasenia);
        if (ok) {
            System.out.println("Contraseña correcta.");
        } else {
            System.out.println("Contraseña incorrecta.");
        }
    }

    // ============================
    // MENÚ SEGURIDAD Y CONEXIONES
    // ============================
    private void menuConexionesSeguridad() {
        int opcion;
        do {
            System.out.println("\n--- MÓDULO 4: SEGURIDAD Y CONEXIONES ---");
            System.out.println("1. Configurar parámetros de seguridad");
            System.out.println("2. Intentar conexión");
            System.out.println("3. Ver historial completo de conexiones");
            System.out.println("4. Ver historial por usuario");
            System.out.println("5. Ver historial por aplicación");
            System.out.println("6. Ver solo conexiones fallidas");
            System.out.println("0. Volver al menú principal");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    configurarSeguridad();
                    break;
                case 2:
                    intentarConexion();
                    break;
                case 3:
                    verHistorialCompleto();
                    break;
                case 4:
                    verHistorialPorUsuario();
                    break;
                case 5:
                    verHistorialPorAplicacion();
                    break;
                case 6:
                    verConexionesFallidas();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            if (opcion != 0) {
                pausar();
            }
        } while (opcion != 0);
    }

    private void configurarSeguridad() {
        System.out.println("\n> Configurar parámetros de seguridad");
        String dominio = leerLinea("Dominio de correo permitido (ej: @institucion.edu, deje vacío para cualquiera): ");
        String prefijoIp = leerLinea("Prefijo de IP permitido (ej: 192.168.1., deje vacío para cualquiera): ");
        int maxIntentos = leerEntero("Máximo de intentos fallidos antes de bloquear usuario: ");

        gestorSeguridad.setDominioCorreoPermitido(dominio);
        gestorSeguridad.setPrefijoIpPermitido(prefijoIp);
        gestorSeguridad.setMaxIntentosFallidos(maxIntentos);

        System.out.println("Parámetros de seguridad actualizados.");
    }

    private void intentarConexion() {
        System.out.println("\n> Intentar conexión");
        String correo = leerLinea("Correo del usuario: ");
        String nombreApp = leerLinea("Nombre de la aplicación: ");
        String contrasenia = leerLinea("Contraseña ingresada: ");
        String ip = leerLinea("Dirección IP de origen: ");

        Conexion con = gestorConexiones.intentarConexion(correo, nombreApp, contrasenia, ip);
        System.out.println("Resultado de la conexión:");
        System.out.println(con);
    }

    private void verHistorialCompleto() {
        System.out.println("\n> Historial completo de conexiones");
        List<Conexion> lista = gestorConexiones.obtenerHistorialCompleto();
        if (lista.isEmpty()) {
            System.out.println("No hay conexiones registradas.");
            return;
        }
        for (Conexion c : lista) {
            System.out.println(c);
        }
    }

    private void verHistorialPorUsuario() {
        System.out.println("\n> Historial de conexiones por usuario");
        Usuario u = seleccionarUsuarioPorCorreo();
        if (u == null) return;
        List<Conexion> lista = gestorConexiones.obtenerHistorialPorUsuario(u);
        if (lista.isEmpty()) {
            System.out.println("No hay conexiones registradas para ese usuario.");
            return;
        }
        for (Conexion c : lista) {
            System.out.println(c);
        }
    }

    private void verHistorialPorAplicacion() {
        System.out.println("\n> Historial de conexiones por aplicación");
        Aplicacion app = seleccionarAplicacionPorId();
        if (app == null) return;
        List<Conexion> lista = gestorConexiones.obtenerHistorialPorAplicacion(app);
        if (lista.isEmpty()) {
            System.out.println("No hay conexiones registradas para esa aplicación.");
            return;
        }
        for (Conexion c : lista) {
            System.out.println(c);
        }
    }

    private void verConexionesFallidas() {
        System.out.println("\n> Conexiones fallidas");
        List<Conexion> lista = gestorConexiones.obtenerConexionesFallidas();
        if (lista.isEmpty()) {
            System.out.println("No hay conexiones fallidas registradas.");
            return;
        }
        for (Conexion c : lista) {
            System.out.println(c);
        }
    }

    // ============================
    // UTILIDADES
    // ============================
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine();
            try {
                return Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private void pausar() {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }
}
