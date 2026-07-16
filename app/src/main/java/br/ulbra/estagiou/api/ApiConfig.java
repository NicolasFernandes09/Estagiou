package br.ulbra.estagiou.api;

import java.net.URL;
import java.util.Locale;

import br.ulbra.estagiou.BuildConfig;

public class ApiConfig {
    public static final String BASE_URL = BuildConfig.API_BASE_URL;
    public static final String VAGAS_URL = BASE_URL + "vagas.php";
    public static final String USUARIOS_URL = BASE_URL + "usuarios.php";

    public static String resolverUrlArquivo(String caminho) {
        if (caminho == null || caminho.trim().isEmpty()) {
            return "";
        }

        String valor = caminho.trim().replace('\\', '/');
        String minusculo = valor.toLowerCase(Locale.ROOT);
        if (minusculo.startsWith("http://")
                || minusculo.startsWith("https://")
                || minusculo.startsWith("content://")
                || minusculo.startsWith("file://")
                || minusculo.startsWith("data:")) {
            return valor;
        }

        try {
            URL base = new URL(BASE_URL);
            if (valor.startsWith("/")) {
                return new URL(base, valor).toString();
            }
            return new URL(new URL(base, "../"), valor).toString();
        } catch (Exception ignored) {
            return valor;
        }
    }
}
