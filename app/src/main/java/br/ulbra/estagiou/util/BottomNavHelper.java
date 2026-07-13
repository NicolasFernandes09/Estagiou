package br.ulbra.estagiou.util;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import br.ulbra.estagiou.R;

public class BottomNavHelper {
    public static void pintarItem(Activity activity, int imgId, int txtId, boolean ativo) {
        int cor = ContextCompat.getColor(activity,
                ativo ? R.color.estagiou_orange_corporate : R.color.estagiou_hint);

        ImageView imagem = activity.findViewById(imgId);
        TextView texto = activity.findViewById(txtId);

        if (imagem != null) {
            imagem.setColorFilter(cor, PorterDuff.Mode.SRC_IN);
        }

        if (texto != null) {
            texto.setTextColor(cor);
            texto.setTypeface(null, ativo ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
    }
}
