import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;

/**
 * Ventana de inicio de sesion del cajero.
 */
public class LoginFrame extends JFrame {
    private final SupabaseService supabaseService;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginFrame(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
        this.usernameField = new JTextField();
        this.passwordField = new JPasswordField();

        configureFrame();
        buildUi();
    }

    private void configureFrame() {
        setTitle("Cajero Automatico - Inicio de sesion");
        setMinimumSize(new Dimension(620, 360));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(UIStyle.COLOR_FONDO);
    }

    private void buildUi() {
        JPanel container = new JPanel(new BorderLayout(0, 18));
        container.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        container.setBackground(UIStyle.COLOR_FONDO);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 18));
        cardPanel.setBorder(UIStyle.createCardBorder());
        UIStyle.stylePanel(cardPanel);

        JLabel titleLabel = new JLabel("Bienvenido al cajero");
        titleLabel.setFont(UIStyle.titleFont());
        titleLabel.setForeground(UIStyle.COLOR_TEXTO);

        JLabel subtitleLabel = new JLabel("Ingresa tu usuario y clave numerica para continuar");
        subtitleLabel.setFont(UIStyle.normalFont());
        subtitleLabel.setForeground(UIStyle.COLOR_TEXTO);

        JPanel titlePanel = new JPanel(new GridBagLayout());
        UIStyle.stylePanel(titlePanel);
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.gridy = 0;
        titleConstraints.anchor = GridBagConstraints.WEST;
        titleConstraints.insets = new Insets(0, 0, 8, 0);
        titlePanel.add(titleLabel, titleConstraints);

        titleConstraints.gridy = 1;
        titleConstraints.insets = new Insets(0, 0, 0, 0);
        titlePanel.add(subtitleLabel, titleConstraints);

        JPanel formPanel = new JPanel(new GridBagLayout());
        UIStyle.stylePanel(formPanel);

        JLabel userLabel = new JLabel("Usuario:");
        JLabel passwordLabel = new JLabel("Clave:");
        UIStyle.styleLabel(userLabel);
        UIStyle.styleLabel(passwordLabel);

        UIStyle.styleField(usernameField);
        UIStyle.styleField(passwordField);

        ((AbstractDocument) usernameField.getDocument()).setDocumentFilter(InputValidators.createLettersFilter());
        ((AbstractDocument) passwordField.getDocument()).setDocumentFilter(InputValidators.createNumericFilter());

        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.insets = new Insets(8, 0, 8, 12);
        formConstraints.anchor = GridBagConstraints.WEST;
        formConstraints.gridx = 0;
        formConstraints.gridy = 0;
        formConstraints.weightx = 0;
        formConstraints.fill = GridBagConstraints.NONE;
        formPanel.add(userLabel, formConstraints);

        formConstraints.gridx = 1;
        formConstraints.weightx = 1;
        formConstraints.fill = GridBagConstraints.HORIZONTAL;
        formConstraints.insets = new Insets(8, 0, 8, 0);
        formPanel.add(usernameField, formConstraints);

        formConstraints.gridx = 0;
        formConstraints.gridy = 1;
        formConstraints.weightx = 0;
        formConstraints.fill = GridBagConstraints.NONE;
        formConstraints.insets = new Insets(8, 0, 8, 12);
        formPanel.add(passwordLabel, formConstraints);

        formConstraints.gridx = 1;
        formConstraints.weightx = 1;
        formConstraints.fill = GridBagConstraints.HORIZONTAL;
        formConstraints.insets = new Insets(8, 0, 8, 0);
        formPanel.add(passwordField, formConstraints);

        JButton loginButton = new JButton("Ingresar");
        UIStyle.stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> attemptLogin());

        JPanel buttonPanel = new JPanel();
        UIStyle.stylePanel(buttonPanel);
        buttonPanel.add(loginButton);

        cardPanel.add(titlePanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        container.add(cardPanel, BorderLayout.CENTER);
        add(container);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Debes ingresar usuario y clave.",
                "Campos obligatorios",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            ATMUser user = supabaseService.autenticarUsuario(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Usuario o clave incorrectos.",
                    "Error de autenticacion",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            BalanceFrame balanceFrame = new BalanceFrame(user, supabaseService);
            balanceFrame.setVisible(true);
            dispose();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Error de conexion",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
