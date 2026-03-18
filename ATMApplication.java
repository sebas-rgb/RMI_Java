import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion.
 */
public class ATMApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applyLookAndFeel();
            SupabaseService supabaseService = new SupabaseService();
            LoginFrame loginFrame = new LoginFrame(supabaseService);
            loginFrame.setVisible(true);
        });
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
