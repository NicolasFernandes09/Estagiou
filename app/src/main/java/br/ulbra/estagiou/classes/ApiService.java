package br.ulbra.estagiou.classes;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("vagas.php")
    Call<ResponseBody> buscarVagas();

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> enviarUsuario(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> login(
            @Field("action") String acao,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> registrar(
            @Field("action") String acao,
            @Field("nome") String nome,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha,
            @Field("descricao_profissional") String descricaoProfissional,
            @Field("descricao_pessoal") String descricaoPessoal,
            @Field("foto") String foto
    );

    @GET("usuarios.php")
    Call<ResponseBody> buscarUsuarios();
}
