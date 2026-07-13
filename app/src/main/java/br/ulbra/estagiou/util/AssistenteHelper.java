package br.ulbra.estagiou.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

public class AssistenteHelper {
    private static final String SHARED_PREFS_NAME = "user_session";
    private static final String KEY_USERNAME = "logged_user";

    public static void mostrarSePreciso(Activity activity, String tela, String titulo, String mensagem) {
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String usuario = preferences.getString(KEY_USERNAME, "");

        if (usuario.equals("")) {
            return;
        }

        String chaveAtivo = "assistente_ativo_" + usuario;
        String chaveTela = "assistente_" + tela + "_" + usuario;

        if (!preferences.getBoolean(chaveAtivo, false) || preferences.getBoolean(chaveTela, false)) {
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("Entendi", (dialog, which) ->
                        preferences.edit().putBoolean(chaveTela, true).apply())
                .show();
    }
}
