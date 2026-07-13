package br.ulbra.estagiou.api;

import android.content.Context;

import org.json.JSONObject;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import br.ulbra.estagiou.repository.SessaoManager;

public class UsuarioApiClient {
    public interface Callback {
        void onSuccess(String mensagem);

        void onError(String mensagem);
    }

    public void login(Context context, String usuario, String email, String senha, Callback callback) {
        SessaoManager.inicializar(context);
        Call<ResponseBody> chamada = RetrofitClient.getApiService()
                .login("login", usuario, email, senha);
        enviar(context, usuario, chamada, true, callback);
    }

    public void registrar(Context context, String nome, String usuario, String email, String senha,
                          String descricaoProfissional, String descricaoPessoal, String foto,
                          Callback callback) {
        SessaoManager.inicializar(context);
        Call<ResponseBody> chamada = RetrofitClient.getApiService()
                .registrar("registrar", nome, usuario, email, senha, descricaoProfissional, descricaoPessoal, foto);
        enviar(context, usuario, chamada, false, callback);
    }

    public void logout(Context context, Callback callback) {
        SessaoManager.inicializar(context);
        Call<ResponseBody> chamada = RetrofitClient.getApiService().logout("logout");
        enviar(context, "", chamada, false, callback);
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

    private void enviar(Context context, String usuario, Call<ResponseBody> chamada,
                        boolean guardarToken, Callback callback) {
        chamada.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String texto = respostaTexto(response);
                if (response.isSuccessful() && respostaOk(texto)) {
                    if (guardarToken) {
                        salvarSessao(context, usuario, texto);
                    }
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

    private void salvarSessao(Context context, String usuario, String resposta) {
        try {
            JSONObject json = new JSONObject(resposta);
            String token = json.optString("token", "");
            int usuarioId = json.optInt("id_usuario", 0);
            JSONObject dadosUsuario = json.optJSONObject("usuario");
            if (dadosUsuario != null) {
                usuarioId = dadosUsuario.optInt("id_usuario", usuarioId);
                usuario = dadosUsuario.optString("usuario", usuario);
            }
            if (!token.isEmpty()) {
                SessaoManager.salvar(context, token, usuarioId, usuario);
            }
        } catch (Exception ignored) {
        }
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
