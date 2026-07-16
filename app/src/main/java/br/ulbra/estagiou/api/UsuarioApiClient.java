package br.ulbra.estagiou.api;

import android.content.Context;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import br.ulbra.estagiou.repository.SessaoManager;
import br.ulbra.estagiou.repository.UsuarioStore;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UsuarioApiClient {
    private enum Operacao {
        LOGIN,
        REGISTRO
    }

    public interface Callback {
        void onSuccess(String mensagem);

        void onError(String mensagem);
    }

    public void login(Context context, String usuario, String email, String senha, Callback callback) {
        Map<String, String> params = parametrosLogin(usuario, email, senha);
        enviar(context, usuario, email, params, Operacao.LOGIN, callback);
    }

    public void registrar(Context context, String nome, String usuario, String email, String senha,
                          String descricaoProfissional, String descricaoPessoal, String foto,
                          Callback callback) {
        Map<String, String> params = parametrosRegistro(
                nome, usuario, email, senha, descricaoProfissional, descricaoPessoal, foto
        );
        enviar(context, usuario, email, params, Operacao.REGISTRO, callback);
    }

    static Map<String, String> parametrosLogin(String usuario, String email, String senha) {
        Map<String, String> params = parametrosAcao("login");
        params.put("usuario", usuario);
        params.put("email", email);
        params.put("senha", senha);
        return params;
    }

    static Map<String, String> parametrosRegistro(String nome, String usuario, String email, String senha,
                                                   String descricaoProfissional, String descricaoPessoal,
                                                   String foto) {
        Map<String, String> params = parametrosAcao("registrar");
        params.put("nome", nome);
        params.put("usuario", usuario);
        params.put("email", email);
        params.put("senha", senha);
        params.put("descricao_profissional", descricaoProfissional);
        params.put("descricao_pessoal", descricaoPessoal);
        params.put("foto", foto);
        return params;
    }

    public void logout(Context context, Callback callback) {
        SessaoManager.limpar(context);
        callback.onSuccess("Sessão encerrada com sucesso.");
    }

    public static boolean respostaOk(String resposta) {
        return respostaOk(resposta, Operacao.REGISTRO);
    }

    static boolean respostaLoginOk(String resposta) {
        return respostaOk(resposta, Operacao.LOGIN);
    }

    private static boolean respostaOk(String resposta, Operacao operacao) {
        String texto = resposta == null ? "" : resposta.trim();
        if (texto.isEmpty()) {
            return false;
        }

        try {
            JSONObject json = new JSONObject(texto);
            if (json.has("success")) {
                return json.optBoolean("success", false);
            }

            JSONObject usuario = json.optJSONObject("usuario");
            if (operacao == Operacao.LOGIN
                    && (usuarioValido(usuario) || usuarioValido(json))) {
                return true;
            }

            String status = json.optString("status", json.optString("situacao", ""));
            String mensagem = json.optString("mensagem", json.optString("message", ""));
            return textoIndicaSucesso(status + " " + mensagem, operacao);
        } catch (Exception ignored) {
            return textoIndicaSucesso(texto, operacao);
        }
    }

    private static boolean usuarioValido(JSONObject usuario) {
        return usuario != null
                && usuario.optInt("id_usuario", 0) > 0
                && !usuario.optString("email", "").isEmpty();
    }

    private static boolean textoIndicaSucesso(String texto, Operacao operacao) {
        String base = texto.toLowerCase(Locale.ROOT).trim();
        if (base.equals("ok") || base.contains("sucesso")) {
            return true;
        }
        if (operacao == Operacao.LOGIN) {
            return base.contains("login efetuado") || base.contains("autenticado");
        }
        return base.contains("cadastrado") || base.contains("criado");
    }

    private static Map<String, String> parametrosAcao(String acao) {
        Map<String, String> params = new HashMap<>();
        params.put("action", acao);
        params.put("acao", acao);
        return params;
    }

    private void enviar(Context context, String usuario, String email,
                        Map<String, String> params, Operacao operacao, Callback callback) {
        SessaoManager.inicializar(context);
        Call<ResponseBody> chamada = RetrofitClient.getApiService().requisicaoUsuario(params);
        chamada.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String texto = respostaTexto(response);
                if (response.isSuccessful() && respostaOk(texto, operacao)) {
                    if (operacao == Operacao.LOGIN) {
                        salvarSessao(context, usuario, email, texto);
                    }
                    callback.onSuccess(texto.trim());
                    return;
                }
                callback.onError(mensagemResposta(texto, response.code()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable erro) {
                if (erro instanceof SocketTimeoutException) {
                    callback.onError("A API demorou para responder. Tente novamente.");
                } else {
                    callback.onError("Não foi possível conectar à API.");
                }
            }
        });
    }

    private void salvarSessao(Context context, String usuario, String emailInformado, String resposta) {
        try {
            JSONObject json = new JSONObject(resposta);
            JSONObject dados = json.optJSONObject("usuario");
            if (dados == null) {
                dados = json;
            }

            int usuarioId = dados.optInt("id_usuario", json.optInt("id_usuario", 0));
            String token = json.optString("token", "");
            String usuarioServidor = dados.optString("usuario", "").trim();
            String usuarioResolvido = usuarioServidor.isEmpty() ? usuario : usuarioServidor;
            String nome = dados.optString("nome", usuarioResolvido);
            String email = dados.optString("email", emailInformado);
            String fotoServidor = dados.optString("foto", "");
            String descricaoProfissionalServidor = dados.optString("descricao_profissional", "");
            String descricaoPessoalServidor = dados.optString("descricao_pessoal", "");

            SessaoManager.salvar(context, token, usuarioId, usuarioResolvido);

            UsuarioStore.UsuarioDados local = UsuarioStore.buscarUsuario(context, usuarioResolvido);
            String foto = local.foto.isEmpty() ? fotoServidor : local.foto;
            String descricaoProfissional = local.descricaoProfissional.isEmpty()
                    ? descricaoProfissionalServidor
                    : local.descricaoProfissional;
            String descricaoPessoal = local.descricaoPessoal.isEmpty()
                    ? descricaoPessoalServidor
                    : local.descricaoPessoal;
            UsuarioStore.salvarUsuario(
                    context,
                    nome.isEmpty() ? usuarioResolvido : nome,
                    usuarioResolvido,
                    email,
                    descricaoProfissional,
                    descricaoPessoal,
                    foto
            );
        } catch (Exception ignored) {
            SessaoManager.salvar(context, "", 0, usuario);
            UsuarioStore.salvarUsuarioSeAusente(context, usuario, emailInformado);
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

    private String mensagemResposta(String resposta, int codigoHttp) {
        String texto = resposta == null ? "" : resposta.trim();
        String minusculo = texto.toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            return codigoHttp >= 500
                    ? "A API está temporariamente indisponível."
                    : "A API não retornou uma resposta válida.";
        }

        if (minusculo.contains("fatal error") || minusculo.startsWith("<html") || minusculo.startsWith("<!doctype")) {
            return "A API encontrou um erro interno. Verifique o servidor e o banco de dados.";
        }

        try {
            JSONObject json = new JSONObject(texto);
            JSONObject erros = json.optJSONObject("erros");
            if (erros != null) {
                Iterator<String> chaves = erros.keys();
                if (chaves.hasNext()) {
                    String detalhe = erros.optString(chaves.next(), "");
                    if (!detalhe.isEmpty()) {
                        return detalhe;
                    }
                }
            }

            String mensagem = json.optString("mensagem", json.optString("message", ""));
            if (!mensagem.isEmpty()) {
                return mensagem;
            }
        } catch (Exception ignored) {
        }

        return "Não foi possível concluir a solicitação à API.";
    }
}
