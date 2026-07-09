package br.ulbra.estagiou.classes.api;

import java.util.List;

import br.ulbra.estagiou.classes.model.Usuarios;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

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
            @Field("usuario") String usuario,
            @Field("email") String email,
            @Field("senha") String senha
    );
    @GET("usuarios.php")
    Call<List<Usuarios>> buscarUsuarios();

    @POST("usuarios.php")
    Call<Void> inserirUsuarios(@Body Usuarios usuario);

    @PUT("usuarios/{id_usuario}")
    Call<Void> atualizarUsuarios(
            @Path("id_usuario") int id,
            @Body Usuarios usuario);

    @DELETE("usuarios/{id_usuario}")
    Call<Void> excluirUsuarios(
            @Path("id_usuario") int id);

}