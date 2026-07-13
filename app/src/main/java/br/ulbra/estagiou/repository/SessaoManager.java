package br.ulbra.estagiou.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SessaoManager {
    private static final String PREFS = "user_session";
    private static final String TOKEN = "auth_token";
    private static final String USUARIO_ID = "auth_user_id";
    private static final String USUARIO = "logged_user";
    private static final String LOGADO = "is_logged";

    private static SharedPreferences preferences;

    public static void inicializar(Context context) {
        if (preferences == null) {
            preferences = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        }
    }

    public static void salvar(Context context, String token, int usuarioId, String usuario) {
        inicializar(context);
        preferences.edit()
                .putString(TOKEN, token)
                .putInt(USUARIO_ID, usuarioId)
                .putString(USUARIO, usuario)
                .putBoolean(LOGADO, token != null && !token.isEmpty())
                .apply();
    }

    public static String token() {
        return preferences == null ? "" : preferences.getString(TOKEN, "");
    }

    public static boolean estaLogado(Context context) {
        inicializar(context);
        return preferences.getBoolean(LOGADO, false) && !token().isEmpty();
    }

    public static void limpar(Context context) {
        inicializar(context);
        preferences.edit().clear().apply();
    }
}
