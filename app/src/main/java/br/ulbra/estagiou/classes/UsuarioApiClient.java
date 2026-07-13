package br.ulbra.estagiou.classes;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
                          String descricaoProfissional, String descricaoPessoal, String fotoUri,
                          Callback callback) {
        Call<ResponseBody> chamada = RetrofitClient.getApiService().registrar(
                textoParte("registrar"),
                textoParte(nome),
                textoParte(usuario),
                textoParte(email),
                textoParte(senha),
                textoParte(descricaoProfissional),
                textoParte(descricaoPessoal),
                parteFoto(context, fotoUri)
        );
        enviar(chamada, callback);
    }

    private RequestBody textoParte(String valor) {
        return RequestBody.create(MediaType.parse("text/plain"), valor == null ? "" : valor);
    }

    // O app guarda apenas a URI local da foto escolhida; aqui lemos os bytes reais
    // do arquivo para enviá-los como upload multipart (é isso que a API espera em $_FILES['foto']).
    private MultipartBody.Part parteFoto(Context context, String fotoUri) {
        if (fotoUri == null || fotoUri.isEmpty()) {
            return MultipartBody.Part.createFormData("foto", "");
        }

        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse(fotoUri);
            String mimeType = resolver.getType(uri);
            if (mimeType == null) {
                mimeType = "image/jpeg";
            }

            byte[] bytes = lerBytes(resolver, uri);
            String extensao = mimeType.contains("png") ? "png"
                    : mimeType.contains("webp") ? "webp"
                    : mimeType.contains("gif") ? "gif"
                    : "jpg";

            RequestBody corpo = RequestBody.create(MediaType.parse(mimeType), bytes);
            return MultipartBody.Part.createFormData("foto", "foto_perfil." + extensao, corpo);
        } catch (Exception e) {
            return MultipartBody.Part.createFormData("foto", "");
        }
    }

    private byte[] lerBytes(ContentResolver resolver, Uri uri) throws Exception {
        try (InputStream entrada = resolver.openInputStream(uri);
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            if (entrada == null) {
                throw new IllegalStateException("Não foi possível abrir a imagem selecionada.");
            }
            byte[] buffer = new byte[8192];
            int lidos;
            while ((lidos = entrada.read(buffer)) != -1) {
                saida.write(buffer, 0, lidos);
            }
            return saida.toByteArray();
        }
    }

    private void enviar(Call<ResponseBody> chamada, Callback callback) {
        chamada.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String texto = respostaTexto(response);
                // O backend já usa o status HTTP corretamente (200/201 sucesso, 4xx/5xx erro),
                // então ele é o sinal de sucesso confiável — não dá pra depender do texto da
                // resposta conter palavras como "sucesso", pois nem todo endpoint as retorna.
                if (response.isSuccessful()) {
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
