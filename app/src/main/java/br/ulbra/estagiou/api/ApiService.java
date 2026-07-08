package br.ulbra.estagiou.api;
import java.util.List;

import br.ulbra.estagiou.model.Vagas;

import retrofit2.Call;
import retrofit2.http.GET;



// Essa interface informa ao Retrofit quais chamadas
// ele pode fazer na API.

public interface ApiService {



    // GET significa que vamos buscar dados.

    // O endereço completo será:
    //
    // http://172.29.20.169/projetoEstagiou/Estagiou/api/vagas.php
    //
    // porque a parte inicial fica no RetrofitClient
    // e aqui colocamos apenas o arquivo.

    @GET("vagas.php")
    Call<List<Vagas>> buscarVagas();


}