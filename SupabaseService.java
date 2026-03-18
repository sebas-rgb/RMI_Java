import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio simple para consultar y actualizar datos en Supabase usando la API REST.
 *
 * Antes de ejecutar:
 * 1. Reemplaza SUPABASE_URL por la URL de tu proyecto.
 * 2. Reemplaza SUPABASE_API_KEY por tu anon key o service role key.
 * 3. Asegurate de tener estas tablas:
 *    usuarios(id, username, password)
 *    cuentas(id, usuario_id, saldo)
 *    transacciones(id, cuenta_id, monto)
 */
public class SupabaseService {
    private static final String SUPABASE_URL = "https://msnsgrjbsbaepnkdjfvy.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1zbnNncmpic2JhZXBua2RqZnZ5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM3MDg3NzYsImV4cCI6MjA4OTI4NDc3Nn0.o0q6R3zCtyv_yTj7SSM1BJrQjQCSomfffBq7onXp7Tg";
    private static final String USERS_TABLE = "usuarios";
    private static final String ACCOUNTS_TABLE = "cuentas";
    private static final String TRANSACTIONS_TABLE = "transacciones";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ATMUser autenticarUsuario(String username, String password) throws IOException {
        validateConfiguration();

        String endpoint = SUPABASE_URL
            + "/rest/v1/" + USERS_TABLE
            + "?select=id,username"
            + "&username=eq." + encodeValue(username)
            + "&password=eq." + encodeValue(password);

        HttpResponse<String> response = sendRequest("GET", endpoint, null, false);
        if (response.statusCode() >= 400) {
            throw new IOException("Error consultando Supabase: " + response.body());
        }

        if (response.body() == null || response.body().trim().equals("[]") || response.body().trim().isEmpty()) {
            return null;
        }

        Integer userId = extractInteger(response.body(), "id");
        String dbUsername = extractString(response.body(), "username");

        if (userId == null || dbUsername == null) {
            return null;
        }

        AccountData accountData = consultarCuentaPorUsuario(userId);
        if (accountData == null) {
            throw new IOException("El usuario existe, pero no tiene una cuenta asociada.");
        }

        return new ATMUser(userId, accountData.cuentaId, dbUsername, accountData.saldo);
    }

    public double actualizarSaldo(int cuentaId, double nuevoSaldo) throws IOException {
        validateConfiguration();

        String endpoint = SUPABASE_URL
            + "/rest/v1/" + ACCOUNTS_TABLE
            + "?id=eq." + cuentaId
            + "&select=saldo";

        String payload = "{\"saldo\":" + nuevoSaldo + "}";
        HttpResponse<String> response = sendRequest("PATCH", endpoint, payload, true);
        if (response.statusCode() >= 400) {
            throw new IOException("Error actualizando saldo en Supabase: " + response.body());
        }

        return parseSaldo(response.body(), nuevoSaldo);
    }

    public void registrarRetiro(int cuentaId, double monto) throws IOException {
        validateConfiguration();

        String endpoint = SUPABASE_URL + "/rest/v1/" + TRANSACTIONS_TABLE;

        String payload = "{\"cuenta_id\":" + cuentaId + ",\"monto\":" + monto + "}";
        HttpResponse<String> response = sendRequest("POST", endpoint, payload, false);
        if (response.statusCode() >= 400) {
            throw new IOException("Error registrando el retiro en Supabase: " + response.body());
        }
    }

    private HttpResponse<String> sendRequest(String method, String endpoint, String body, boolean returnRepresentation)
        throws IOException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(java.net.URI.create(endpoint))
                .header("apikey", SUPABASE_API_KEY)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .header("Accept", "application/json");

            if (body != null) {
                builder.header("Content-Type", "application/json");
                if (returnRepresentation) {
                    builder.header("Prefer", "return=representation");
                }
                builder.method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("La solicitud a Supabase fue interrumpida.", exception);
        }
    }

    private void validateConfiguration() throws IOException {
        if (SUPABASE_URL.contains("TU-PROYECTO") || SUPABASE_API_KEY.contains("TU_SUPABASE_API_KEY")) {
            throw new IOException("Debes configurar tu URL y API KEY de Supabase en SupabaseService.java");
        }
    }

    private String encodeValue(String value) throws IOException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    }

    private double parseSaldo(String json, double fallback) {
        Double saldo = extractDouble(json, "saldo");
        return saldo != null ? saldo : fallback;
    }

    private AccountData consultarCuentaPorUsuario(int userId) throws IOException {
        String endpoint = SUPABASE_URL
            + "/rest/v1/" + ACCOUNTS_TABLE
            + "?select=id,saldo"
            + "&usuario_id=eq." + userId;

        HttpResponse<String> response = sendRequest("GET", endpoint, null, false);
        if (response.statusCode() >= 400) {
            throw new IOException("Error consultando la cuenta en Supabase: " + response.body());
        }

        Integer cuentaId = extractInteger(response.body(), "id");
        Double saldo = extractDouble(response.body(), "saldo");

        if (cuentaId == null || saldo == null) {
            return null;
        }

        return new AccountData(cuentaId, saldo);
    }

    private Integer extractInteger(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":(\\d+)").matcher(json);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private Double extractDouble(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":(-?\\d+(?:\\.\\d+)?)").matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return null;
    }

    private String extractString(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\":\"([^\"]+)\"").matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static class AccountData {
        private final int cuentaId;
        private final double saldo;

        private AccountData(int cuentaId, double saldo) {
            this.cuentaId = cuentaId;
            this.saldo = saldo;
        }
    }
}
