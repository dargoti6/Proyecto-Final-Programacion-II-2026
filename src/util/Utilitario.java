package util;

import javax.swing.*;
import java.awt.*;

public final class Utilitario {

    private Utilitario() {}

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component parent, String pregunta) {
        int op = JOptionPane.showConfirmDialog(parent, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION);
        return op == JOptionPane.YES_OPTION;
    }

    public static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
