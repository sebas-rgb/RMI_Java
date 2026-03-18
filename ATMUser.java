import java.text.NumberFormat;
import java.util.Locale;

/**
 * Modelo que representa el usuario autenticado en el cajero.
 */
public class ATMUser {
    private static final Locale LOCALE_CO = new Locale.Builder()
        .setLanguage("es")
        .setRegion("CO")
        .build();

    private final int id;
    private final int cuentaId;
    private final String username;
    private double saldo;

    public ATMUser(int id, int cuentaId, String username, double saldo) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.username = username;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public int getCuentaId() {
        return cuentaId;
    }

    public String getUsername() {
        return username;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getSaldoFormateado() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(LOCALE_CO);
        return currencyFormat.format(saldo);
    }
}
