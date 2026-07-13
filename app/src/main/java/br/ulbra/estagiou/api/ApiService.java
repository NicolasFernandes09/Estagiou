package br.ulbra.estagiou.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import br.ulbra.estagiou.model.Vagas;

public interface ApiService {
    @GET("vagas.php")
    Call<ResponseBody> buscarVagas();

    @POST("vagas.php")
    Call<ResponseBody> inserirVaga(@Body Vagas vaga);

    @PUT("vagas.php")
    Call<ResponseBody> atualizarVaga(@Query("id") int id, @Body Vagas vaga);

    @DELETE("vagas.php")
    Call<ResponseBody> excluirVaga(@Query("id") int id);

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> enviarUsuario(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> login(
            @Field("acao") String acao,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> registrar(
            @Field("acao") String acao,
            @Field("nome") String nome,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha,
            @Field("descricao_profissional") String descricaoProfissional,
            @Field("descricao_pessoal") String descricaoPessoal,
            @Field("foto") String foto
    );

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> logout(@Field("acao") String acao);

    @GET("usuarios.php")
    Call<ResponseBody> buscarUsuarios();
}
