package interfaz;

import negocio.Sistema;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Sistema sistema = new Sistema();
            AppFrame frame = new AppFrame(sistema);
            frame.setVisible(true);
        });
    }
}
