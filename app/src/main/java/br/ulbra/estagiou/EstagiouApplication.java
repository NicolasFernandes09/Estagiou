package br.ulbra.estagiou;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import br.ulbra.estagiou.activity.VagasActivity;
import br.ulbra.estagiou.repository.SessaoManager;
import br.ulbra.estagiou.util.ApiStatusManager;

public class EstagiouApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG_STATUS = "status_api_estagiou";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private WeakReference<Activity> activityAtual = new WeakReference<>(null);

    @Override
    public void onCreate() {
        super.onCreate();
        SessaoManager.inicializar(this);
        registerActivityLifecycleCallbacks(this);
        agendarVerificacaoDasVinteHoras();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityAtual = new WeakReference<>(activity);
        mostrarIndicador(activity);
        if (!(activity instanceof VagasActivity)) {
            verificarApi(false);
        }
    }

    public void definirStatusApi(boolean online) {
        ApiStatusManager.definirStatus(online);
        atualizarIndicadorAtual();
    }

    private void verificarApi(boolean forcar) {
        ApiStatusManager.verificar(forcar, online -> atualizarIndicadorAtual());
    }

    private void mostrarIndicador(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        View anterior = content.findViewWithTag(TAG_STATUS);
        if (anterior != null) {
            content.removeView(anterior);
        }

        View indicador = new View(activity);
        indicador.setId(R.id.viewStatusApiGlobal);
        indicador.setTag(TAG_STATUS);
        indicador.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        indicador.setElevation(dp(activity, 8));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(activity, 18), dp(activity, 18));
        params.gravity = Gravity.TOP | Gravity.END;
        params.topMargin = dp(activity, 18);
        params.setMarginEnd(dp(activity, 18));
        content.addView(indicador, params);
        atualizarIndicador(indicador, ApiStatusManager.online());
    }

    private void atualizarIndicadorAtual() {
        Activity activity = activityAtual.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.runOnUiThread(() -> {
            View indicador = activity.findViewById(android.R.id.content).findViewWithTag(TAG_STATUS);
            if (indicador != null) {
                atualizarIndicador(indicador, ApiStatusManager.online());
            }
        });
    }

    private void atualizarIndicador(View indicador, boolean online) {
        indicador.setBackgroundResource(online ? R.drawable.bg_api_online : R.drawable.bg_api_offline);
        indicador.setContentDescription(online ? "API conectada" : "API desconectada");
    }

    private void agendarVerificacaoDasVinteHoras() {
        Calendar agora = Calendar.getInstance();
        Calendar proxima = Calendar.getInstance();
        proxima.set(Calendar.HOUR_OF_DAY, 20);
        proxima.set(Calendar.MINUTE, 0);
        proxima.set(Calendar.SECOND, 0);
        proxima.set(Calendar.MILLISECOND, 0);
        if (!proxima.after(agora)) {
            proxima.add(Calendar.DAY_OF_MONTH, 1);
        }

        handler.postDelayed(() -> {
            Activity activity = activityAtual.get();
            if (activity instanceof VagasActivity) {
                ((VagasActivity) activity).recarregarAutomaticamente();
            } else {
                verificarApi(true);
            }
            agendarVerificacaoDasVinteHoras();
        }, proxima.getTimeInMillis() - agora.getTimeInMillis());
    }

    private int dp(Activity activity, int valor) {
        return (int) (valor * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
