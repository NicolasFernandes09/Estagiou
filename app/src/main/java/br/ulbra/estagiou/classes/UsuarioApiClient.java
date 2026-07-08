package br.ulbra.estagiou.classes;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UsuarioApiClient {
    public interface Callback {
        void onSuccess(String mensagem);

        void onError(String mensagem);
    }

    public void login(Context context, String usuario, String email, String senha, Callback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("acao", "login");
        params.put("usuario", usuario);
        params.put("email", email);
        params.put("senha", senha);
        enviar(context, params, callback);
    }

    public void registrar(Context context, String nome, String usuario, String email, String senha,
                          String descricaoProfissional, String descricaoPessoal, String foto,
                          Callback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("acao", "registrar");
        params.put("nome", nome);
        params.put("usuario", usuario);
        params.put("email", email);
        params.put("senha", senha);
        params.put("descricao_profissional", descricaoProfissional);
        params.put("descricao_pessoal", descricaoPessoal);
        params.put("foto", foto);
        enviar(context, params, callback);
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
            return base.contains("sucesso") || base.equals("ok") || base.contains(" ok");
        } catch (Exception ignored) {
            String base = texto.toLowerCase(Locale.ROOT);
            return base.equals("ok") || base.contains("sucesso");
        }
    }

    private void enviar(Context context, Map<String, String> params, Callback callback) {
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.VAGAS_URL,
                response -> {
                    if (respostaOk(response)) {
                        callback.onSuccess(response.trim());
                    } else {
                        callback.onError(mensagemResposta(response));
                    }
                },
                error -> callback.onError("Não foi possível conectar à API")) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        request.setShouldCache(false);
        queue.add(request);
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
