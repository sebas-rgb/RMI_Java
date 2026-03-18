import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Ventana que muestra el saldo disponible del usuario.
 */
public class BalanceFrame extends JFrame {
    private final ATMUser user;
    private final SupabaseService supabaseService;
    private final JLabel saldoLabel;
    private final JLabel usuarioLabel;

    public BalanceFrame(ATMUser user, SupabaseService supabaseService) {
        this.user = user;
        this.supabaseService = supabaseService;
        this.saldoLabel = new JLabel("", SwingConstants.CENTER);
        this.usuarioLabel = new JLabel("", SwingConstants.CENTER);

        configureFrame();
        buildUi();
        refreshBalance();
    }

    private void configureFrame() {
        setTitle("Cajero Automatico - Saldo");
        setMinimumSize(new Dimension(620, 380));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(UIStyle.COLOR_FONDO);
    }

    private void buildUi() {
        JPanel container = new JPanel(new BorderLayout(0, 18));
        container.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        container.setBackground(UIStyle.COLOR_FONDO);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 16));
        cardPanel.setBorder(UIStyle.createCardBorder());
        UIStyle.stylePanel(cardPanel);

        JLabel titleLabel = new JLabel("Consulta de saldo", SwingConstants.CENTER);
        titleLabel.setFont(UIStyle.titleFont());
        titleLabel.setForeground(UIStyle.COLOR_TEXTO);

        usuarioLabel.setFont(UIStyle.normalFont());
        usuarioLabel.setForeground(UIStyle.COLOR_TEXTO);

        JLabel currentBalanceLabel = new JLabel("Saldo actual", SwingConstants.CENTER);
        currentBalanceLabel.setFont(UIStyle.subtitleFont());
        currentBalanceLabel.setForeground(UIStyle.COLOR_TEXTO);

        saldoLabel.setFont(UIStyle.titleFont());
        saldoLabel.setForeground(UIStyle.COLOR_PRIMARIO);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        UIStyle.stylePanel(infoPanel);
        infoPanel.add(titleLabel);
        infoPanel.add(usuarioLabel);
        infoPanel.add(currentBalanceLabel);
        infoPanel.add(saldoLabel);

        JButton withdrawButton = new JButton("Retirar dinero");
        JButton logoutButton = new JButton("Cerrar sesion");
        UIStyle.stylePrimaryButton(withdrawButton);
        UIStyle.styleSecondaryButton(logoutButton);

        withdrawButton.addActionListener(e -> openWithdrawFrame());
        logoutButton.addActionListener(e -> returnToLogin());

        JPanel buttonPanel = new JPanel();
        UIStyle.stylePanel(buttonPanel);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(logoutButton);

        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        container.add(cardPanel, BorderLayout.CENTER);
        add(container);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void refreshBalance() {
        usuarioLabel.setText("Usuario: " + user.getUsername());
        saldoLabel.setText(user.getSaldoFormateado());
    }

    private void openWithdrawFrame() {
        WithdrawFrame withdrawFrame = new WithdrawFrame(user, supabaseService, this);
        withdrawFrame.setVisible(true);
        setVisible(false);
    }

    private void returnToLogin() {
        LoginFrame loginFrame = new LoginFrame(supabaseService);
        loginFrame.setVisible(true);
        dispose();
    }
}
