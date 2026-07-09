package br.ulbra.estagiou.classes;

import android.content.Context;

import org.json.JSONObject;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UsuarioApiClient {
    public interface Callback {
        void onSuccess(String mensagem);

        void onError(String mensagem);
    }

    public void login(Context context, String usuario, String email, String senha, Callback callback) {
        Call<ResponseBody> chamada = RetrofitClient.getApiService()
                .login("login", usuario, email, senha);
        enviar(chamada, callback);
    }

    public void registrar(Context context, String nome, String usuario, String email, String senha,
                          String descricaoProfissional, String descricaoPessoal, String foto,
                          Callback callback) {
        Call<ResponseBody> chamada = RetrofitClient.getApiService()
                .registrar("registrar", nome, usuario, email, senha, descricaoProfissional, descricaoPessoal, foto);
        enviar(chamada, callback);
    }

    public static boolean respostaOk(String resposta) {
        String texto = resposta == null ? "" : resposta.trim();
        if (texto.equals("")) {
            return false;
        }

        try {
            JSONObject json = new JSONObject(texto);
            if (json.optBoolean("success", false)) {
                return true;
            }
            String status = json.optString("status", json.optString("situacao", ""));
            String mensagem = json.optString("mensagem", json.optString("message", ""));
            String base = (status + " " + mensagem).toLowerCase(Locale.ROOT);
            return base.contains("sucesso")
                    || base.equals("ok")
                    || base.contains(" ok")
                    || base.contains("cadastrado")
                    || base.contains("login efetuado");
        } catch (Exception ignored) {
            String base = texto.toLowerCase(Locale.ROOT);
            return base.equals("ok")
                    || base.contains("sucesso")
                    || base.contains("cadastrado")
                    || base.contains("login efetuado");
        }
    }

    private void enviar(Call<ResponseBody> chamada, Callback callback) {
        chamada.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String texto = respostaTexto(response);
                if (response.isSuccessful() && respostaOk(texto)) {
                    callback.onSuccess(texto.trim());
                } else {
                    callback.onError(mensagemResposta(texto));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Não foi possível conectar à API");
            }
        });
    }

    private String respostaTexto(Response<ResponseBody> response) {
        try {
            if (response.body() != null) {
                return response.body().string();
            }
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private String mensagemResposta(String resposta) {
        String texto = resposta == null ? "" : resposta.trim();
        if (texto.equals("")) {
            return "A API não retornou uma resposta válida";
        }

        try {
            JSONObject json = new JSONObject(texto);
            String mensagem = json.optString("mensagem", json.optString("message", ""));
            if (!mensagem.equals("")) {
                return mensagem;
            }
        } catch (Exception ignored) {
        }

        return texto;
    }
}
