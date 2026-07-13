package br.ulbra.estagiou.util;

import br.ulbra.estagiou.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ApiStatusManager {
    public interface Callback {
        void onResult(boolean online);
    }

    private static final long INTERVALO = 15000;
    private static boolean online;
    private static boolean verificado;
    private static boolean verificando;
    private static long ultimaVerificacao;

    public static boolean online() {
        return online;
    }

    public static void definirStatus(boolean status) {
        online = status;
        verificado = true;
        ultimaVerificacao = System.currentTimeMillis();
    }

    public static void verificar(boolean forcar, Callback callback) {
        long agora = System.currentTimeMillis();
        if (!forcar && verificado && agora - ultimaVerificacao < INTERVALO) {
            callback.onResult(online);
            return;
        }

        if (verificando) {
            callback.onResult(online);
            return;
        }

        verificando = true;
        Call<ResponseBody> chamada = RetrofitClient.getApiService().buscarVagas();
        chamada.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    response.body().close();
                }
                if (response.errorBody() != null) {
                    response.errorBody().close();
                }
                verificando = false;
                definirStatus(response.isSuccessful());
                callback.onResult(online);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                verificando = false;
                definirStatus(false);
                callback.onResult(false);
            }
        });
    }
}
