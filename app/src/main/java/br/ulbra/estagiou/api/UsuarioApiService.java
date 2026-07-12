package br.ulbra.estagiou.api;

import java.util.List;

import br.ulbra.estagiou.model.Usuarios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UsuarioApiService {

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
            @Field("nome") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );
    @GET("usuarios.php")
    Call<List<Usuarios>> buscarUsuarios();

    @POST("usuarios.php")
    Call<Void> inserirUsuarios(@Body Usuarios usuario);

    @PUT("usuarios.php")
    Call<Void> atualizarUsuarios(
            @Query("id") int id,
            @Body Usuarios usuario);

    @DELETE("usuarios.php")
    Call<Void> excluirUsuarios(
            @Query("id") int id);

}
