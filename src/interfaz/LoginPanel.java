package interfaz;

import negocio.*;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class LoginPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    // Crear usuario
    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtFdn = new JTextField(10); // yyyy-MM-dd
    private final JTextField txtCedula = new JTextField(12);
    private final JTextField txtCorreo = new JTextField(18);
    private final JPasswordField txtPass1 = new JPasswordField(18);
    private final JPasswordField txtPass2 = new JPasswordField(18);

    // Login
    private final JTextField txtIdent = new JTextField(18); // correo o cédula
    private final JPasswordField txtPassLogin = new JPasswordField(18);

    public LoginPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10,10));
        JLabel title = new JLabel("Ventana 1 - Registro / Inicio de Sesión");
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Crear Usuario", buildCrearUsuario());
        tabs.addTab("Iniciar Sesión", buildLogin());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildCrearUsuario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints c = base();

        addRow(p, c, 0, "Nombre:", txtNombre);
        addRow(p, c, 1, "Fecha Nac (yyyy-MM-dd):", txtFdn);
        addRow(p, c, 2, "Cédula:", txtCedula);
        addRow(p, c, 3, "Correo:", txtCorreo);
        addRow(p, c, 4, "Contraseña:", txtPass1);
        addRow(p, c, 5, "Verificar Contraseña:", txtPass2);

        JButton btn = new JButton("Crear Usuario");
        btn.addActionListener(e -> onCrearUsuario());
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        p.add(btn, c);

        JLabel note = new JLabel("Nota: sin persistencia. Al cerrar, se pierde todo. Admin por defecto: admin@system.local / Admin1234");
        c.gridy = 7;
        p.add(note, c);

        return p;
    }

    private JPanel buildLogin() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints c = base();

        addRow(p, c, 0, "Correo o Cédula:", txtIdent);
        addRow(p, c, 1, "Contraseña:", txtPassLogin);

        JButton btn = new JButton("Iniciar Sesión");
        btn.addActionListener(e -> onLogin());
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        p.add(btn, c);

        return p;
    }

    private void onCrearUsuario() {
        try {
            String nombre = Utilitario.safe(txtNombre.getText());
            String fdnStr = Utilitario.safe(txtFdn.getText());
            String cedula = Utilitario.safe(txtCedula.getText());
            String correo = Utilitario.safe(txtCorreo.getText());
            String p1 = new String(txtPass1.getPassword());
            String p2 = new String(txtPass2.getPassword());

            LocalDate fdn;
            try {
                fdn = LocalDate.parse(fdnStr);
            } catch (Exception ex) {
                throw new ValidacionException("Fecha inválida. Usa formato yyyy-MM-dd.");
            }

            sistema.registrarUsuario(nombre, fdn, cedula, correo, p1, p2);
            Utilitario.info(this, "Usuario creado correctamente. Ahora puedes iniciar sesión.");
            limpiarRegistro();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void onLogin() {
        try {
            String ident = Utilitario.safe(txtIdent.getText());
            String pass = new String(txtPassLogin.getPassword());

            Usuario u = sistema.iniciarSesion(ident, pass);
            frame.setUsuarioActual(u);
            frame.showDashboard();
        } catch (Exception ex) {
            Utilitario.error(this, ex.getMessage());
        }
    }

    private void limpiarRegistro() {
        txtNombre.setText("");
        txtFdn.setText("");
        txtCedula.setText("");
        txtCorreo.setText("");
        txtPass1.setText("");
        txtPass2.setText("");
    }

    private GridBagConstraints base() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
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
