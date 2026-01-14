package interfaz;

import negocio.Sistema;
import negocio.Usuario;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private final Sistema sistema;
    private Usuario usuarioActual;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final LoginPanel loginPanel;
    private final DashboardPanel dashboardPanel;

    // Paneles (por ahora placeholders, los implementamos en el siguiente paso)
    private final AppsPanel appsPanel;
    private final CredencialesPanel credencialesPanel;
    private final ConexionesPanel conexionesPanel;
    private final PerfilPanel perfilPanel;
    private final AdminPanel adminPanel;

    public AppFrame(Sistema sistema) {
        super("Sistema de Gestión de Usuarios");
        this.sistema = sistema;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(this, sistema);
        dashboardPanel = new DashboardPanel(this, sistema);

        appsPanel = new AppsPanel(this, sistema);
        credencialesPanel = new CredencialesPanel(this, sistema);
        conexionesPanel = new ConexionesPanel(this, sistema);
        perfilPanel = new PerfilPanel(this, sistema);
        adminPanel = new AdminPanel(this, sistema);

        cards.add(loginPanel, "LOGIN");
        cards.add(dashboardPanel, "DASH");
        cards.add(appsPanel, "APPS");
        cards.add(credencialesPanel, "CREDS");
        cards.add(conexionesPanel, "CONEX");
        cards.add(perfilPanel, "PERFIL");
        cards.add(adminPanel, "ADMIN");

        setContentPane(cards);
        showLogin();
    }

    public Sistema getSistema() { return sistema; }
    public Usuario getUsuarioActual() { return usuarioActual; }

    public void setUsuarioActual(Usuario u) {
        this.usuarioActual = u;
        dashboardPanel.refresh(u);
        appsPanel.refresh(u);
        credencialesPanel.refresh(u);
        conexionesPanel.refresh(u);
        perfilPanel.refresh(u);
        adminPanel.refresh(u);
    }

    public void showLogin() {
        setUsuarioActual(null);
        cardLayout.show(cards, "LOGIN");
    }

    public void showDashboard() {
        dashboardPanel.refresh(usuarioActual);
        cardLayout.show(cards, "DASH");
    }

    public void showApps() { appsPanel.refresh(usuarioActual); cardLayout.show(cards, "APPS"); }
    public void showCredenciales() { credencialesPanel.refresh(usuarioActual); cardLayout.show(cards, "CREDS"); }
    public void showConexiones() { conexionesPanel.refresh(usuarioActual); cardLayout.show(cards, "CONEX"); }
    public void showPerfil() { perfilPanel.refresh(usuarioActual); cardLayout.show(cards, "PERFIL"); }
    public void showAdmin() { adminPanel.refresh(usuarioActual); cardLayout.show(cards, "ADMIN"); }
}
