package interfaz;

import negocio.*;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConexionesPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    // Intentar conexión
    private final JTextField txtApp = new JTextField(18);
    private final JTextField txtCredId = new JTextField(28);
    private final JPasswordField txtSecreto = new JPasswordField(18);
    private final JTextArea outIntento = new JTextArea(10, 50);

    // Historial
    private final JTextArea outHist = new JTextArea(16, 50);

    // Admin: historial completo
    private final JTextArea outHistGlobal = new JTextArea(16, 50);

    // Estado
    private final JLabel lblEstado = new JLabel("-");

    public ConexionesPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Ventana 3 - 2.3 Conexiones");
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("2.3.1 Intentar conexión", buildIntentar());
        tabs.addTab("2.3.2 Requisitos conexión", buildRequisitos());
        tabs.addTab("2.3.3 Historial conexiones", buildHistorial());
        tabs.addTab("2.3.4 Estado conexiones", buildEstado());

        add(tabs, BorderLayout.CENTER);

        JButton back = new JButton("Volver");
        back.addActionListener(e -> frame.showDashboard());
        add(back, BorderLayout.SOUTH);
    }

    private JPanel buildIntentar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "Nombre App:", txtApp);
        addRow(form, c, 1, "ID Credencial:", txtCredId);
        addRow(form, c, 2, "Secreto (intento):", txtSecreto);

        JButton btn = new JButton("Intentar conexión");
        btn.addActionListener(e -> onIntentar());
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        form.add(btn, c);

        JLabel hint = new JLabel("Sugerencia: ve a 2.2 Listar credenciales y copia el ID.");
        c.gridy = 4;
        form.add(hint, c);

        outIntento.setEditable(false);
        outIntento.setLineWrap(true);
        outIntento.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outIntento), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRequisitos() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JTextArea area = new JTextArea(10, 50);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JButton btn = new JButton("Ver requisitos");
        btn.addActionListener(e -> area.setText(sistema.verRequisitosConexion().toString()));

        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHistorial() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnUser = new JButton("Ver mi historial");
        JButton btnGlobal = new JButton("Ver historial completo (Admin)");
        btnUser.addActionListener(e -> onHistorialUsuario());
        btnGlobal.addActionListener(e -> onHistorialGlobal());
        top.add(btnUser);
        top.add(btnGlobal);

        outHist.setEditable(false);
        outHist.setLineWrap(true);
        outHist.setWrapStyleWord(true);

        outHistGlobal.setEditable(false);
        outHistGlobal.setLineWrap(true);
        outHistGlobal.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(outHist),
                new JScrollPane(outHistGlobal));
        split.setResizeWeight(0.5);

        p.add(top, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildEstado() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btn = new JButton("Actualizar estado");
        btn.addActionListener(e -> lblEstado.setText(sistema.estadoConexiones()));
        top.add(btn);

        lblEstado.setFont(lblEstado.getFont().deriveFont(14f));

        p.add(top, BorderLayout.NORTH);
        p.add(lblEstado, BorderLayout.CENTER);
        return p;
    }

    private void onIntentar() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            IntentoConexion intento = sistema.intentarConexion(
                    u,
                    Utilitario.safe(txtApp.getText()),
                    Utilitario.safe(txtCredId.getText()),
                    new String(txtSecreto.getPassword())
            );

            outIntento.setText("Resultado del intento:\n" + intento +
                    "\n\nNota: se generó una notificación en la bandeja (2.4.3) para confirmar si fuiste tú.");
            // refrescar estado/historial local
            onHistorialUsuario();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onHistorialUsuario() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { outHist.setText(""); return; }

        List<IntentoConexion> hist = u.getHistorialConexiones();
        StringBuilder sb = new StringBuilder();
        sb.append("Mi historial (").append(hist.size()).append("):\n\n");
        for (IntentoConexion ic : hist) sb.append(ic).append("\n");
        outHist.setText(sb.toString());
    }

    private void onHistorialGlobal() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { outHistGlobal.setText(""); return; }

        try {
            List<IntentoConexion> hist = sistema.verHistorialConexionesCompleto(u);
            StringBuilder sb = new StringBuilder();
            sb.append("Historial completo (").append(hist.size()).append("):\n\n");
            for (IntentoConexion ic : hist) sb.append(ic).append("\n");
            outHistGlobal.setText(sb.toString());
        } catch (Exception ex) {
            outHistGlobal.setText("No disponible: " + ex.getMessage());
        }
    }

    public void refresh(Usuario u) {
        outIntento.setText("");
        onHistorialUsuario();
        outHistGlobal.setText("");
        lblEstado.setText("-");
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
