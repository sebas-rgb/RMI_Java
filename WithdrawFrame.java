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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;

/**
 * Ventana para realizar retiros y actualizar el saldo.
 */
public class WithdrawFrame extends JFrame {
    private final ATMUser user;
    private final SupabaseService supabaseService;
    private final BalanceFrame balanceFrame;
    private final JTextField amountField;
    private final JLabel remainingBalanceLabel;

    public WithdrawFrame(ATMUser user, SupabaseService supabaseService, BalanceFrame balanceFrame) {
        this.user = user;
        this.supabaseService = supabaseService;
        this.balanceFrame = balanceFrame;
        this.amountField = new JTextField();
        this.remainingBalanceLabel = new JLabel("", SwingConstants.CENTER);

        configureFrame();
        buildUi();
        updateRemainingBalance();
    }

    private void configureFrame() {
        setTitle("Cajero Automatico - Retiro");
        setMinimumSize(new Dimension(640, 400));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyle.COLOR_FONDO);
    }

    private void buildUi() {
        JPanel container = new JPanel(new BorderLayout(0, 18));
        container.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        container.setBackground(UIStyle.COLOR_FONDO);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 18));
        cardPanel.setBorder(UIStyle.createCardBorder());
        UIStyle.stylePanel(cardPanel);

        JLabel titleLabel = new JLabel("Retiro de dinero", SwingConstants.CENTER);
        titleLabel.setFont(UIStyle.titleFont());
        titleLabel.setForeground(UIStyle.COLOR_TEXTO);

        JLabel messageLabel = new JLabel("Ingresa el valor que deseas retirar", SwingConstants.CENTER);
        messageLabel.setFont(UIStyle.normalFont());
        messageLabel.setForeground(UIStyle.COLOR_TEXTO);

        JPanel headerPanel = new JPanel(new GridBagLayout());
        UIStyle.stylePanel(headerPanel);
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;
        headerConstraints.insets = new Insets(0, 0, 8, 0);
        headerPanel.add(titleLabel, headerConstraints);
        headerConstraints.gridy = 1;
        headerConstraints.insets = new Insets(0, 0, 0, 0);
        headerPanel.add(messageLabel, headerConstraints);

        JLabel amountLabel = new JLabel("Monto a retirar:");
        JLabel balanceTextLabel = new JLabel("Saldo restante:");
        UIStyle.styleLabel(amountLabel);
        UIStyle.styleLabel(balanceTextLabel);

        UIStyle.styleField(amountField);
        ((AbstractDocument) amountField.getDocument()).setDocumentFilter(InputValidators.createNumericFilter());

        remainingBalanceLabel.setFont(UIStyle.subtitleFont());
        remainingBalanceLabel.setForeground(UIStyle.COLOR_PRIMARIO);

        JPanel formPanel = new JPanel(new GridBagLayout());
        UIStyle.stylePanel(formPanel);

        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.gridx = 0;
        formConstraints.gridy = 0;
        formConstraints.insets = new Insets(8, 0, 8, 12);
        formConstraints.anchor = GridBagConstraints.WEST;
        formPanel.add(amountLabel, formConstraints);

        formConstraints.gridx = 1;
        formConstraints.weightx = 1;
        formConstraints.fill = GridBagConstraints.HORIZONTAL;
        formConstraints.insets = new Insets(8, 0, 8, 0);
        formPanel.add(amountField, formConstraints);

        formConstraints.gridx = 0;
        formConstraints.gridy = 1;
        formConstraints.weightx = 0;
        formConstraints.fill = GridBagConstraints.NONE;
        formConstraints.insets = new Insets(8, 0, 8, 12);
        formPanel.add(balanceTextLabel, formConstraints);

        formConstraints.gridx = 1;
        formConstraints.weightx = 1;
        formConstraints.fill = GridBagConstraints.HORIZONTAL;
        formConstraints.insets = new Insets(8, 0, 8, 0);
        formPanel.add(remainingBalanceLabel, formConstraints);

        JButton confirmButton = new JButton("Confirmar retiro");
        JButton backButton = new JButton("Volver");
        UIStyle.stylePrimaryButton(confirmButton);
        UIStyle.styleSecondaryButton(backButton);

        confirmButton.addActionListener(e -> processWithdrawal());
        backButton.addActionListener(e -> goBack());

        JPanel buttonPanel = new JPanel();
        UIStyle.stylePanel(buttonPanel);
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        container.add(cardPanel, BorderLayout.CENTER);
        add(container);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void processWithdrawal() {
        String amountText = amountField.getText().trim();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Debes ingresar un monto.",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Solo se permiten numeros en el retiro.",
                "Dato invalido",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "El monto debe ser mayor a cero.",
                "Validacion",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (amount > user.getSaldo()) {
            JOptionPane.showMessageDialog(
                this,
                "No tienes saldo suficiente para este retiro.",
                "Saldo insuficiente",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        double nuevoSaldo = user.getSaldo() - amount;

        try {
            double saldoActualizado = supabaseService.actualizarSaldo(user.getCuentaId(), nuevoSaldo);
            supabaseService.registrarRetiro(user.getCuentaId(), amount);
            user.setSaldo(saldoActualizado);
            updateRemainingBalance();
            balanceFrame.refreshBalance();

            JOptionPane.showMessageDialog(
                this,
                "Retiro realizado correctamente.",
                "Operacion exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );
            amountField.setText("");
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Error al actualizar saldo",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateRemainingBalance() {
        remainingBalanceLabel.setText(user.getSaldoFormateado());
    }

    private void goBack() {
        balanceFrame.refreshBalance();
        balanceFrame.setVisible(true);
        dispose();
    }
}
