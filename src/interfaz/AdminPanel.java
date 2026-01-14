package interfaz;

import negocio.*;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    // Listados
    private final JTextArea outUsuarios = new JTextArea(16, 60);
    private final JTextArea outApps = new JTextArea(16, 60);

    // Usuarios
    private final JTextField txtCedulaUser = new JTextField(14);
    private final JTextArea outUserOps = new JTextArea(6, 60);

    // Apps
    private final JTextField txtNombreApp = new JTextField(18);
    private final JTextArea outAppOps = new JTextArea(6, 60);

    // Parámetros seguridad
    private final JTextField txtMinLen = new JTextField(6);
    private final JCheckBox chkUpper = new JCheckBox("Requiere mayúscula");
    private final JCheckBox chkLower = new JCheckBox("Requiere minúscula");
    private final JCheckBox chkDigit = new JCheckBox("Requiere dígito");
    private final JTextField txtMaxIntentos = new JTextField(6);
    private final JTextArea outParams = new JTextArea(6, 60);

    // Historial por usuario
    private final JTextField txtCedulaHist = new JTextField(14);
    private final JTextArea outHistUser = new JTextArea(16, 60);

    // Vulnerabilidades
    private final JTextArea outVuln = new JTextArea(6, 60);

    // Reporte
    private final JTextArea outReporte = new JTextArea(20, 60);

    public AdminPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Ventana 3 - 2.5 Gestión Usuarios (Solo Admin)");
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("2.5.1 Listar usuarios", buildListarUsuarios());
        tabs.addTab("2.5.2 Listar aplicaciones", buildListarApps());
        tabs.addTab("2.5.3-2.5.4 Usuarios (bloquear/activar)", buildUsuariosOps());
        tabs.addTab("2.5.5-2.5.6 Apps (bloquear/activar)", buildAppsOps());
        tabs.addTab("2.5.7 Parámetros de seguridad", buildParams());
        tabs.addTab("2.5.8 Historial conexiones por usuario", buildHistPorUsuario());
        tabs.addTab("2.5.9 Revisión vulnerabilidades", buildVuln());
        tabs.addTab("2.5.10 Generar reporte", buildReporte());

        add(tabs, BorderLayout.CENTER);

        JButton back = new JButton("Volver");
        back.addActionListener(e -> frame.showDashboard());
        add(back, BorderLayout.SOUTH);
    }

    private JPanel buildListarUsuarios() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        outUsuarios.setEditable(false);
        outUsuarios.setLineWrap(true);
        outUsuarios.setWrapStyleWord(true);

        JButton btn = new JButton("Refrescar");
        btn.addActionListener(e -> cargarUsuarios());

        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(outUsuarios), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildListarApps() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        outApps.setEditable(false);
        outApps.setLineWrap(true);
        outApps.setWrapStyleWord(true);

        JButton btn = new JButton("Refrescar");
        btn.addActionListener(e -> cargarApps());

        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(outApps), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildUsuariosOps() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        form.add(new JLabel("Cédula:"));
        form.add(txtCedulaUser);

        JButton b1 = new JButton("Bloquear");
        JButton b2 = new JButton("Activar");
        b1.addActionListener(e -> onBloquearUsuario());
        b2.addActionListener(e -> onActivarUsuario());

        form.add(b1);
        form.add(b2);

        outUserOps.setEditable(false);
        outUserOps.setLineWrap(true);
        outUserOps.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outUserOps), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAppsOps() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        form.add(new JLabel("Nombre App:"));
        form.add(txtNombreApp);

        JButton b1 = new JButton("Bloquear app");
        JButton b2 = new JButton("Activar app");
        b1.addActionListener(e -> onBloquearApp());
        b2.addActionListener(e -> onActivarApp());

        form.add(b1);
        form.add(b2);

        outAppOps.setEditable(false);
        outAppOps.setLineWrap(true);
        outAppOps.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outAppOps), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildParams() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "Min. longitud password:", txtMinLen);
        addRow(form, c, 1, "Max. intentos fallidos login:", txtMaxIntentos);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checks.add(chkUpper);
        checks.add(chkLower);
        checks.add(chkDigit);
        form.add(checks, c);

        JButton btn = new JButton("Guardar parámetros");
        btn.addActionListener(e -> onGuardarParams());
        c.gridy = 3;
        form.add(btn, c);

        JButton btnVer = new JButton("Cargar parámetros actuales");
        btnVer.addActionListener(e -> cargarParamsDesdeSistema());
        c.gridy = 4;
        form.add(btnVer, c);

        outParams.setEditable(false);
        outParams.setLineWrap(true);
        outParams.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outParams), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHistPorUsuario() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.add(new JLabel("Cédula:"));
        top.add(txtCedulaHist);

        JButton btn = new JButton("Ver historial");
        btn.addActionListener(e -> onHistPorUsuario());
        top.add(btn);

        outHistUser.setEditable(false);
        outHistUser.setLineWrap(true);
        outHistUser.setWrapStyleWord(true);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(outHistUser), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildVuln() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JButton btn = new JButton("Revisar vulnerabilidades");
        btn.addActionListener(e -> onVulnerabilidades());

        outVuln.setEditable(false);
        outVuln.setLineWrap(true);
        outVuln.setWrapStyleWord(true);

        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(outVuln), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildReporte() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JButton btn = new JButton("Generar reporte");
        btn.addActionListener(e -> onReporte());

        outReporte.setEditable(false);
        outReporte.setLineWrap(true);
        outReporte.setWrapStyleWord(true);

        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(outReporte), BorderLayout.CENTER);
        return p;
    }

    private void cargarUsuarios() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            List<Usuario> usuarios = sistema.listarUsuarios(admin);
            StringBuilder sb = new StringBuilder();
            sb.append("Usuarios (").append(usuarios.size()).append(")\n\n");
            for (Usuario u : usuarios) sb.append(u).append("\n");
            outUsuarios.setText(sb.toString());
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void cargarApps() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            List<Aplicacion> apps = sistema.listarAplicaciones(admin);
            StringBuilder sb = new StringBuilder();
            sb.append("Aplicaciones (").append(apps.size()).append(")\n\n");
            for (Aplicacion a : apps) sb.append(a).append("\n");
            outApps.setText(sb.toString());
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onBloquearUsuario() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            sistema.bloquearUsuario(admin, Utilitario.safe(txtCedulaUser.getText()));
            outUserOps.setText("Usuario bloqueado correctamente.");
            cargarUsuarios();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onActivarUsuario() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            sistema.activarUsuario(admin, Utilitario.safe(txtCedulaUser.getText()));
            outUserOps.setText("Usuario activado correctamente.");
            cargarUsuarios();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onBloquearApp() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            sistema.bloquearAplicacion(admin, Utilitario.safe(txtNombreApp.getText()));
            outAppOps.setText("Aplicación bloqueada correctamente.");
            cargarApps();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onActivarApp() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            sistema.activarAplicacion(admin, Utilitario.safe(txtNombreApp.getText()));
            outAppOps.setText("Aplicación activada correctamente.");
            cargarApps();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void cargarParamsDesdeSistema() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        ParametrosSeguridad p = sistema.verRequisitosConexion();
        txtMinLen.setText(String.valueOf(p.getMinPasswordLength()));
        txtMaxIntentos.setText(String.valueOf(p.getMaxIntentosFallidosLogin()));
        chkUpper.setSelected(p.isRequireUpper());
        chkLower.setSelected(p.isRequireLower());
        chkDigit.setSelected(p.isRequireDigit());

        outParams.setText("Parámetros actuales:\n" + p);
    }

    private void onGuardarParams() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            int minLen = Integer.parseInt(Utilitario.safe(txtMinLen.getText()));
            int maxInt = Integer.parseInt(Utilitario.safe(txtMaxIntentos.getText()));

            ParametrosSeguridad nuevos = new ParametrosSeguridad();
            nuevos.setMinPasswordLength(minLen);
            nuevos.setMaxIntentosFallidosLogin(maxInt);
            nuevos.setRequireUpper(chkUpper.isSelected());
            nuevos.setRequireLower(chkLower.isSelected());
            nuevos.setRequireDigit(chkDigit.isSelected());

            sistema.actualizarParametrosSeguridad(admin, nuevos);
            outParams.setText("Parámetros guardados correctamente.\n" + sistema.verRequisitosConexion());
        } catch (NumberFormatException nfe) {
            Utilitario.error(this, "Valores numéricos inválidos.");
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onHistPorUsuario() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            List<IntentoConexion> hist = sistema.verHistorialConexionesPorUsuario(admin, Utilitario.safe(txtCedulaHist.getText()));
            StringBuilder sb = new StringBuilder();
            sb.append("Historial del usuario (").append(hist.size()).append(")\n\n");
            for (IntentoConexion ic : hist) sb.append(ic).append("\n");
            outHistUser.setText(sb.toString());
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onVulnerabilidades() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            outVuln.setText(sistema.revisionVulnerabilidades(admin));
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onReporte() {
        Usuario admin = frame.getUsuarioActual();
        if (!isAdmin(admin)) return;

        try {
            outReporte.setText(sistema.generarReporte(admin));
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private boolean isAdmin(Usuario u) {
        if (u == null) {
            Utilitario.error(this, "No hay sesión activa.");
            return false;
        }
        if (u.getRol() != Rol.ADMIN) {
            Utilitario.error(this, "Acceso denegado: solo administrador.");
            return false;
        }
        return true;
    }

    public void refresh(Usuario u) {
        // Si no es admin, limpiamos.
        outUsuarios.setText("");
        outApps.setText("");
        outUserOps.setText("");
        outAppOps.setText("");
        outParams.setText("");
        outHistUser.setText("");
        outVuln.setText("");
        outReporte.setText("");

        if (u != null && u.getRol() == Rol.ADMIN) {
            cargarParamsDesdeSistema();
        }
    }

    private GridBagConstraints base() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.gridwidth = 1; c.weightx = 0;
        p.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        p.add(field, c);
    }
}
