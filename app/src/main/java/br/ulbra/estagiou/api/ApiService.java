package br.ulbra.estagiou.api;
import java.util.List;

import br.ulbra.estagiou.model.Vagas;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


// Essa interface informa ao Retrofit quais chamadas
// ele pode fazer na API.

public interface ApiService {



    // GET significa que vamos buscar dados.

    // O endereço completo será:
    //
    // http://172.29.20.89/projetoEstagiou/Estagiou/api/vagas.php
    //
    // porque a parte inicial fica no RetrofitClient
    // e aqui colocamos apenas o arquivo.
    @GET("vagas.php")
    Call<List<Vagas>> buscarVagas();

    @POST("vagas.php")
    Call<Void> inserirVagas(@Body Vagas vaga);

    @PUT("vagas/{id}")
    Call<Void> atualizarVagas(
            @Path("id") int id,
            @Body Vagas vaga);

    @DELETE("vagas/{id}")
    Call<Void> excluirVagas(
            @Path("id") int id);
}