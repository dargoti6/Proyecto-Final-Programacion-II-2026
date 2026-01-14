package interfaz;

import negocio.*;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppsPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    private final JTextField txtRegNombre = new JTextField(18);
    private final JTextField txtRegIp = new JTextField(18);
    private final JTextArea outRegistro = new JTextArea(8, 50);

    private final JTextField txtBuscarNombre = new JTextField(18);
    private final JTextArea outBuscar = new JTextArea(10, 50);

    private final JTextField txtUpdNombre = new JTextField(18);
    private final JTextField txtUpdIp = new JTextField(18);
    private final JTextArea outUpdate = new JTextArea(8, 50);

    private final JTextField txtHistNombre = new JTextField(18);
    private final JTextArea outHistorial = new JTextArea(14, 50);

    public AppsPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Ventana 3 - 2.1 Gestión Aplicaciones");
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("2.1.1 Registrar", buildRegistrar());
        tabs.addTab("2.1.2 Buscar", buildBuscar());
        tabs.addTab("2.1.3 Actualizar IP", buildActualizarIp());
        tabs.addTab("2.1.4 Historial de uso", buildHistorial());

        add(tabs, BorderLayout.CENTER);

        JButton back = new JButton("Volver");
        back.addActionListener(e -> frame.showDashboard());
        add(back, BorderLayout.SOUTH);
    }

    private JPanel buildRegistrar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "Nombre App:", txtRegNombre);
        addRow(form, c, 1, "IP (IPv4):", txtRegIp);

        JButton btn = new JButton("Registrar aplicación");
        btn.addActionListener(e -> onRegistrar());
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        form.add(btn, c);

        outRegistro.setEditable(false);
        outRegistro.setLineWrap(true);
        outRegistro.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outRegistro), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBuscar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();
        addRow(form, c, 0, "Nombre App:", txtBuscarNombre);

        JButton btn = new JButton("Buscar");
        btn.addActionListener(e -> onBuscar());
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        form.add(btn, c);

        outBuscar.setEditable(false);
        outBuscar.setLineWrap(true);
        outBuscar.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outBuscar), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildActualizarIp() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();
        addRow(form, c, 0, "Nombre App:", txtUpdNombre);
        addRow(form, c, 1, "Nueva IP (IPv4):", txtUpdIp);

        JButton btn = new JButton("Actualizar IP");
        btn.addActionListener(e -> onActualizarIp());
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        form.add(btn, c);

        outUpdate.setEditable(false);
        outUpdate.setLineWrap(true);
        outUpdate.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outUpdate), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHistorial() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();
        addRow(form, c, 0, "Nombre App:", txtHistNombre);

        JButton btn = new JButton("Ver historial de uso");
        btn.addActionListener(e -> onHistorial());
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        form.add(btn, c);

        outHistorial.setEditable(false);
        outHistorial.setLineWrap(true);
        outHistorial.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outHistorial), BorderLayout.CENTER);
        return p;
    }

    private void onRegistrar() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            Aplicacion app = sistema.registrarAplicacion(
                    u,
                    Utilitario.safe(txtRegNombre.getText()),
                    Utilitario.safe(txtRegIp.getText())
            );
            outRegistro.setText("Aplicación registrada:\n" + app);
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onBuscar() {
        try {
            Aplicacion app = sistema.buscarAplicacion(Utilitario.safe(txtBuscarNombre.getText()));
            outBuscar.setText("Aplicación encontrada:\n" + app);
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onActualizarIp() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            sistema.actualizarIpAplicacion(
                    u,
                    Utilitario.safe(txtUpdNombre.getText()),
                    Utilitario.safe(txtUpdIp.getText())
            );
            outUpdate.setText("IP actualizada correctamente.");
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onHistorial() {
        try {
            List<IntentoConexion> hist = sistema.historialUsoAplicacion(Utilitario.safe(txtHistNombre.getText()));
            StringBuilder sb = new StringBuilder();
            sb.append("Historial de uso (").append(hist.size()).append("):\n\n");
            for (IntentoConexion ic : hist) sb.append(ic).append("\n");
            outHistorial.setText(sb.toString());
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    public void refresh(Usuario u) {
        // No hace falta bloquear la vista, pero limpiamos outputs si no hay sesión.
        if (u == null) {
            outRegistro.setText("");
            outBuscar.setText("");
            outUpdate.setText("");
            outHistorial.setText("");
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
