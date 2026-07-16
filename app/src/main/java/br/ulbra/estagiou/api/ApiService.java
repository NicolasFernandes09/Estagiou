package br.ulbra.estagiou.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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

    @Multipart
    @POST("usuarios.php")
    Call<ResponseBody> requisicaoUsuarioComFoto(
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part foto
    );

    @GET("usuarios.php")
    Call<ResponseBody> buscarUsuarios();

    @GET("usuarios.php")
    Call<ResponseBody> buscarUsuario(@Query("id") int id);
}
