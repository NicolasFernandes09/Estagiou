package br.ulbra.estagiou.classes;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/vagas.php")
    Call<ResponseBody> login(
            @Field("acao") String acao,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );

    @FormUrlEncoded
    @POST("api/vagas.php")
    Call<ResponseBody> registrar(
            @Field("acao") String acao,
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );

}