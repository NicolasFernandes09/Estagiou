package br.ulbra.estagiou.util;

import android.app.Activity;
import android.view.View;

public class TelaHelper {
    public static void preencherPainel(Activity activity, int painelId, int descontoDp) {
        View painel = activity.findViewById(painelId);
        if (painel == null) {
            return;
        }

        painel.post(() -> {
            int alturaTela = activity.getResources().getDisplayMetrics().heightPixels;
            int alturaMinima = alturaTela - dp(activity, descontoDp);
            painel.setMinimumHeight(Math.max(painel.getMinimumHeight(), alturaMinima));
        });
    }

    private static int dp(Activity activity, int valor) {
        return (int) (valor * activity.getResources().getDisplayMetrics().density + 0.5f);
    }
}
