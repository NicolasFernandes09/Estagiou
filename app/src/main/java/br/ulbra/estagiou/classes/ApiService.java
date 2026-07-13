package br.ulbra.estagiou.classes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @GET("vagas.php")
    Call<ResponseBody> buscarVagas();

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> login(
            @Field("action") String acao,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );

    // Multipart porque o cadastro pode incluir uma foto de perfil real (arquivo),
    // e não apenas o caminho/URI local dela.
    @Multipart
    @POST("usuarios.php")
    Call<ResponseBody> registrar(
            @Part("action") RequestBody acao,
            @Part("nome") RequestBody nome,
            @Part("usuario") RequestBody usuario,
            @Part("email") RequestBody email,
            @Part("senha") RequestBody senha,
            @Part("descricao_profissional") RequestBody descricaoProfissional,
            @Part("descricao_pessoal") RequestBody descricaoPessoal,
            @Part MultipartBody.Part foto
    );
}
