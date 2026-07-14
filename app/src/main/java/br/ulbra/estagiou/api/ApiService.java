package br.ulbra.estagiou.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    Call<ResponseBody> atualizarVaga(@Query("id_vaga") int id, @Body Vagas vaga);

    @DELETE("vagas.php")
    Call<ResponseBody> excluirVaga(@Query("id_vaga") int id);

    @FormUrlEncoded
    @POST("usuarios.php")
    Call<ResponseBody> requisicaoUsuario(@FieldMap Map<String, String> params);

    @GET("usuarios.php")
    Call<ResponseBody> buscarUsuarios();

    @GET("usuarios.php")
    Call<ResponseBody> buscarUsuario(@Query("id") int id);
}
