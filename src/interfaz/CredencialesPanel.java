package interfaz;

import negocio.*;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CredencialesPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    private final DefaultListModel<Credencial> modelCreds = new DefaultListModel<>();
    private final JList<Credencial> listCreds = new JList<>(modelCreds);

    // Crear credencial
    private final JComboBox<TipoCredencial> cbTipo = new JComboBox<>(TipoCredencial.values());
    private final JTextField txtAlias = new JTextField(18);
    private final JTextField txtUserAsoc = new JTextField(18);
    private final JPasswordField txtSecreto = new JPasswordField(18);

    private final JTextField txtShift = new JTextField(6);     // para César
    private final JTextField txtKeyVig = new JTextField(14);   // para Vigenere

    private final JTextArea outCrear = new JTextArea(8, 50);

    // Verificar
    private final JTextField txtVerifId = new JTextField(28);
    private final JPasswordField txtVerifIntento = new JPasswordField(18);
    private final JTextArea outVerif = new JTextArea(6, 50);

    // Eliminar
    private final JTextField txtDelId = new JTextField(28);
    private final JTextArea outDel = new JTextArea(6, 50);

    public CredencialesPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Ventana 3 - 2.2 Gestión Credenciales");
        add(title, BorderLayout.NORTH);

        listCreds.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCreds.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Credencial c) {
                    setText(c.getTipo() + " | " + c.getAlias() + " | " + c.getUsernameAsociado() + " | ID=" + c.getId());
                }
                return this;
            }
        });

        listCreds.addListSelectionListener(e -> {
            Credencial c = listCreds.getSelectedValue();
            if (c != null) {
                txtVerifId.setText(c.getId());
                txtDelId.setText(c.getId());
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("2.2.1-2.2.3 Crear", buildCrear());
        tabs.addTab("2.2.4 Listar", buildListar());
        tabs.addTab("2.2.5 Verificar", buildVerificar());
        tabs.addTab("2.2.6 Eliminar", buildEliminar());

        add(tabs, BorderLayout.CENTER);

        JButton back = new JButton("Volver");
        back.addActionListener(e -> frame.showDashboard());
        add(back, BorderLayout.SOUTH);

        cbTipo.addActionListener(e -> toggleCamposTipo());
        toggleCamposTipo();
    }

    private JPanel buildCrear() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "Tipo:", cbTipo);
        addRow(form, c, 1, "Alias/Descripción:", txtAlias);
        addRow(form, c, 2, "Usuario asociado:", txtUserAsoc);
        addRow(form, c, 3, "Secreto/Contraseña:", txtSecreto);
        addRow(form, c, 4, "Shift (César):", txtShift);
        addRow(form, c, 5, "Clave (Vigenere):", txtKeyVig);

        JButton btn = new JButton("Crear credencial");
        btn.addActionListener(e -> onCrear());
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        form.add(btn, c);

        outCrear.setEditable(false);
        outCrear.setLineWrap(true);
        outCrear.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outCrear), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildListar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.add(new JLabel("Credenciales del usuario actual (selecciona una para autocompletar ID):"), BorderLayout.NORTH);
        p.add(new JScrollPane(listCreds), BorderLayout.CENTER);

        JButton btn = new JButton("Refrescar lista");
        btn.addActionListener(e -> cargarCredenciales());
        p.add(btn, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildVerificar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "ID Credencial:", txtVerifId);
        addRow(form, c, 1, "Intento (contraseña):", txtVerifIntento);

        JButton btn = new JButton("Verificar");
        btn.addActionListener(e -> onVerificar());
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        form.add(btn, c);

        outVerif.setEditable(false);
        outVerif.setLineWrap(true);
        outVerif.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outVerif), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildEliminar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = base();

        addRow(form, c, 0, "ID Credencial:", txtDelId);

        JButton btn = new JButton("Eliminar");
        btn.addActionListener(e -> onEliminar());
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        form.add(btn, c);

        outDel.setEditable(false);
        outDel.setLineWrap(true);
        outDel.setWrapStyleWord(true);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(outDel), BorderLayout.CENTER);
        return p;
    }

    private void toggleCamposTipo() {
        TipoCredencial t = (TipoCredencial) cbTipo.getSelectedItem();
        boolean cesar = (t == TipoCredencial.CESAR);
        boolean vig = (t == TipoCredencial.VIGENERE);

        txtShift.setEnabled(cesar);
        txtKeyVig.setEnabled(vig);

        if (!cesar) txtShift.setText("");
        if (!vig) txtKeyVig.setText("");
    }

    private void onCrear() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            TipoCredencial tipo = (TipoCredencial) cbTipo.getSelectedItem();
            String alias = Utilitario.safe(txtAlias.getText());
            String userAsoc = Utilitario.safe(txtUserAsoc.getText());
            String secreto = new String(txtSecreto.getPassword());

            Integer shift = null;
            String key = null;

            if (tipo == TipoCredencial.CESAR) {
                String s = Utilitario.safe(txtShift.getText());
                if (s.isEmpty()) throw new ValidacionException("Debes indicar el shift para César.");
                try { shift = Integer.parseInt(s); }
                catch (NumberFormatException nfe) { throw new ValidacionException("Shift inválido."); }
            } else if (tipo == TipoCredencial.VIGENERE) {
                key = Utilitario.safe(txtKeyVig.getText());
            }

            Credencial c = sistema.crearCredencial(u, tipo, alias, userAsoc, secreto, shift, key);
            outCrear.setText("Credencial creada:\n" + c + "\n\nID: " + c.getId());
            cargarCredenciales();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onVerificar() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            String id = Utilitario.safe(txtVerifId.getText());
            String intento = new String(txtVerifIntento.getPassword());

            boolean ok = sistema.verificarCredencial(u, id, intento);
            outVerif.setText(ok ? "Verificación: CORRECTA" : "Verificación: INCORRECTA");
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onEliminar() {
        Usuario u = frame.getUsuarioActual();
        if (u == null) { Utilitario.error(this, "No hay sesión activa."); return; }

        try {
            String id = Utilitario.safe(txtDelId.getText());
            if (!Utilitario.confirmar(this, "¿Eliminar la credencial con ID " + id + "?")) return;

            sistema.eliminarCredencial(u, id);
            outDel.setText("Credencial eliminada correctamente.");
            cargarCredenciales();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void cargarCredenciales() {
        modelCreds.clear();
        Usuario u = frame.getUsuarioActual();
        if (u == null) return;

        try {
            List<Credencial> creds = sistema.listarCredenciales(u);
            for (Credencial c : creds) modelCreds.addElement(c);
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    public void refresh(Usuario u) {
        cargarCredenciales();
        if (u == null) {
            outCrear.setText("");
            outVerif.setText("");
            outDel.setText("");
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
