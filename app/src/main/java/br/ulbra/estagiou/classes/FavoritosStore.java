package br.ulbra.estagiou.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoritosStore {
    private static final String PREFS_NAME = "favoritos_vagas";
    private static final String KEY_IDS = "ids_vagas_favoritas";

    public static boolean isFavorita(Context context, String vagaId) {
        return getIds(context).contains(vagaId);
    }

    public static Set<String> getIds(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new HashSet<>(preferences.getStringSet(KEY_IDS, new HashSet<>()));
    }

    public static void setFavorita(Context context, String vagaId, boolean favorita) {
        Set<String> ids = getIds(context);
        if (favorita) {
            ids.add(vagaId);
        } else {
            ids.remove(vagaId);
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putStringSet(KEY_IDS, ids)
                .apply();
    }
}
