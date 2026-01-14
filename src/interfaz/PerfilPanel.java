package interfaz;

import negocio.*;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PerfilPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    // 2.4.1 Cambiar contraseña
    private final JPasswordField txtActual = new JPasswordField(18);
    private final JPasswordField txtNueva = new JPasswordField(18);
    private final JPasswordField txtConfirm = new JPasswordField(18);
    private final JTextArea outPass = new JTextArea(6, 50);

    // 2.4.2 Biografía
    private final JTextArea txtBioEdit = new JTextArea(8, 50);

    // 2.4.3 Bandeja
    private final DefaultListModel<Notificacion> modelNotifs = new DefaultListModel<>();
    private final JList<Notificacion> listNotifs = new JList<>(modelNotifs);
    private final JTextArea outNotif = new JTextArea(6, 50);
    private final JCheckBox chkSoloPendientes = new JCheckBox("Solo pendientes", true);

    // 2.4.4 Eliminar cuenta
    private final JPasswordField txtDelPass = new JPasswordField(18);
    private final JTextArea outDel = new JTextArea(6, 50);

    public PerfilPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Ventana 3 - 2.4 Configuración Perfil");
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("2.4.1 Cambiar contraseña", buildCambiarPass());
        tabs.addTab("2.4.2 Editar biografía", buildBio());
        tabs.addTab("2.4.3 Bandeja de entrada", buildBandeja());
        tabs.addTab("2.4.4 Eliminar cuenta", buildEliminar());
        tabs.addTab("2.4.5 Cerrar sesión", buildCerrarSesion());

        add(tabs, BorderLayout.CENTER);

        JButton back = new JButton("Volver");
        back.addActionListener(e -> frame.showDashboard());
        add(back, BorderLayout.SOUTH);

        listNotifs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listNotifs.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Notificacion n) {
                    setText(n.getFechaHora() + " | " + n.getRespuesta() + " | ID=" + n.getId());
                }
                return this;
            }
        });
        listNotifs.addListSelectionListener(e -> mostrarNotifSeleccionada());
        chkSoloPendientes.addActionListener(e -> cargarNotificaciones());
    }

    private JPanel buildCambiarPass() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "Contraseña actual:", txtActual);
        addRow(form, c, 1, "Nueva contraseña:", txtNueva);
        addRow(form, c, 2, "Confirmar nueva:", txtConfirm);

        JButton btn = new JButton("Cambiar contraseña");
        btn.addActionListener(e -> onCambiarPass());
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        form.add(btn, c);

        outPass.setEditable(false);
        outPass.setLineWrap(true);
        outPass.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outPass), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBio() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        txtBioEdit.setLineWrap(true);
        txtBioEdit.setWrapStyleWord(true);

        JButton btn = new JButton("Guardar biografía/estado");
        btn.addActionListener(e -> onGuardarBio());

        p.add(new JScrollPane(txtBioEdit), BorderLayout.CENTER);
        p.add(btn, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildBandeja() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnRef = new JButton("Refrescar");
        btnRef.addActionListener(e -> cargarNotificaciones());
        top.add(btnRef);
        top.add(chkSoloPendientes);

        outNotif.setEditable(false);
        outNotif.setLineWrap(true);
        outNotif.setWrapStyleWord(true);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnSi = new JButton("Responder: SI fui yo");
        JButton btnNo = new JButton("Responder: NO fui yo");
        btnSi.addActionListener(e -> onResponder(RespuestaNotificacion.SI));
        btnNo.addActionListener(e -> onResponder(RespuestaNotificacion.NO));
        actions.add(btnSi);
        actions.add(btnNo);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(listNotifs),
                new JScrollPane(outNotif));
        split.setResizeWeight(0.6);

        p.add(top, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildEliminar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();
        addRow(form, c, 0, "Confirmar contraseña:", txtDelPass);

        JButton btn = new JButton("Eliminar cuenta");
        btn.addActionListener(e -> onEliminarCuenta());
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        form.add(btn, c);

        outDel.setEditable(false);
        outDel.setLineWrap(true);
        outDel.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outDel), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCerrarSesion() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btn = new JButton("Cerrar sesión ahora");
        btn.addActionListener(e -> {
            if (Utilitario.confirmar(this, "¿Cerrar sesión?")) frame.showLogin();
        });
        p.add(btn);
        return p;
    }

    private void onCambiarPass() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            sistema.cambiarPassword(
                    u,
                    new String(txtActual.getPassword()),
                    new String(txtNueva.getPassword()),
                    new String(txtConfirm.getPassword())
            );
            outPass.setText("Contraseña actualizada correctamente.");
            txtActual.setText("");
            txtNueva.setText("");
            txtConfirm.setText("");
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onGuardarBio() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            sistema.editarBiografia(u, txtBioEdit.getText());
            Utilitario.info(this, "Biografía/estado actualizado.");
            // refrescar dashboard cuando vuelvas
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void cargarNotificaciones() {
        modelNotifs.clear();
        Usuario u = frame.getUsuarioActual();
        if (u == null) return;

        try {
            boolean soloPend = chkSoloPendientes.isSelected();
            List<Notificacion> notifs = sistema.listarNotificaciones(u, soloPend);
            for (Notificacion n : notifs) modelNotifs.addElement(n);
            outNotif.setText("");
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void mostrarNotifSeleccionada() {
        Notificacion n = listNotifs.getSelectedValue();
        if (n == null) { outNotif.setText(""); return; }
        outNotif.setText("ID: " + n.getId() +
                "\nFecha: " + n.getFechaHora() +
                "\nRespuesta: " + n.getRespuesta() +
                "\n\nMensaje:\n" + n.getMensaje());
    }

    private void onResponder(RespuestaNotificacion r) {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        Notificacion n = listNotifs.getSelectedValue();
        if (n == null) { Utilitario.error(this, "Selecciona una notificación."); return; }

        try {
            sistema.responderNotificacion(u, n.getId(), r);
            Utilitario.info(this, "Respuesta registrada: " + r);
            cargarNotificaciones();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onEliminarCuenta() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        if (!Utilitario.confirmar(this, "¿Seguro que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")) return;

        try {
            sistema.eliminarCuenta(u, new String(txtDelPass.getPassword()));
            outDel.setText("Cuenta eliminada correctamente. Se cerrará la sesión.");
            frame.showLogin();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    public void refresh(Usuario u) {
        if (u == null) {
            txtBioEdit.setText("");
            modelNotifs.clear();
            outNotif.setText("");
            outPass.setText("");
            outDel.setText("");
            return;
        }
        txtBioEdit.setText(u.getBiografia());
        cargarNotificaciones();
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
