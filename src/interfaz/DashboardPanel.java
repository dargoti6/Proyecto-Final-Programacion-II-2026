package interfaz;

import negocio.Rol;
import negocio.Sistema;
import negocio.Usuario;
import util.Utilitario;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final AppFrame frame;
    private final Sistema sistema;

    private final JLabel lblUsuario = new JLabel("Usuario: -");
    private final JTextArea txtBio = new JTextArea(5, 40);

    private final JButton btnAdmin = new JButton("2.5 Gestión Usuarios (Admin)");

    public DashboardPanel(AppFrame frame, Sistema sistema) {
        this.frame = frame;
        this.sistema = sistema;

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel title = new JLabel("Ventana 2 - Menú Principal");
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10,10));
        center.add(lblUsuario, BorderLayout.NORTH);

        txtBio.setEditable(false);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        center.add(new JScrollPane(txtBio), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(0,1,8,8));
        JButton b1 = new JButton("2.1 Gestión Aplicaciones");
        JButton b2 = new JButton("2.2 Gestión Credenciales");
        JButton b3 = new JButton("2.3 Conexiones");
        JButton b4 = new JButton("2.4 Configuración Perfil");
        JButton b5 = new JButton("Cerrar Sesión");

        b1.addActionListener(e -> frame.showApps());
        b2.addActionListener(e -> frame.showCredenciales());
        b3.addActionListener(e -> frame.showConexiones());
        b4.addActionListener(e -> frame.showPerfil());
        btnAdmin.addActionListener(e -> frame.showAdmin());
        b5.addActionListener(e -> {
            if (Utilitario.confirmar(this, "¿Cerrar sesión?")) frame.showLogin();
        });

        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        buttons.add(b4);
        buttons.add(btnAdmin);
        buttons.add(b5);

        add(buttons, BorderLayout.EAST);
    }

    public void refresh(Usuario u) {
        if (u == null) {
            lblUsuario.setText("Usuario: -");
            txtBio.setText("");
            btnAdmin.setEnabled(false);
            btnAdmin.setVisible(false);
            return;
        }

        lblUsuario.setText("Usuario: " + u.getNombre() + " | " + u.getCorreo() + " | " + u.getRol());
        txtBio.setText(u.getBiografia());

        boolean isAdmin = u.getRol() == Rol.ADMIN;
        btnAdmin.setVisible(isAdmin);
        btnAdmin.setEnabled(isAdmin);
    }
}
