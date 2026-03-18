import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Estilos visuales para que las ventanas se vean menos basicas.
 */
public final class UIStyle {
    public static final Color COLOR_FONDO = new Color(241, 245, 249);
    public static final Color COLOR_PANEL = Color.WHITE;
    public static final Color COLOR_PRIMARIO = new Color(15, 118, 110);
    public static final Color COLOR_SECUNDARIO = new Color(226, 232, 240);
    public static final Color COLOR_TEXTO = new Color(30, 41, 59);

    private static final Border CAMPO_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(203, 213, 225)),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    );

    private UIStyle() {
    }

    public static Font titleFont() {
        return new Font("Segoe UI", Font.BOLD, 24);
    }

    public static Font subtitleFont() {
        return new Font("Segoe UI", Font.BOLD, 16);
    }

    public static Font normalFont() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(normalFont());
        label.setForeground(COLOR_TEXTO);
    }

    public static void styleField(JTextField field) {
        field.setFont(normalFont());
        field.setBorder(CAMPO_BORDER);
        field.setPreferredSize(new Dimension(220, 38));
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFont(subtitleFont());
        button.setBackground(COLOR_PRIMARIO);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(subtitleFont());
        button.setBackground(COLOR_SECUNDARIO);
        button.setForeground(COLOR_TEXTO);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        );
    }

    public static void stylePanel(JComponent component) {
        component.setBackground(COLOR_PANEL);
    }
}
