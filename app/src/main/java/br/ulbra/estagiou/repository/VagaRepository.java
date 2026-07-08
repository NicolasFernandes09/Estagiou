package br.ulbra.estagiou.repository;

import br.ulbra.estagiou.api.ApiService;
import br.ulbra.estagiou.api.RetrofitClient;
import br.ulbra.estagiou.model.Vagas;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

// O Repository fica responsável por conversar com a API.
// A Activity não precisa saber como a API funciona.

public class VagaRepository {

    private ApiService api;

    // Construtor da classe
    public VagaRepository() {

        // Cria o serviço da API usando Retrofit.
        api = RetrofitClient.getRetrofit().create(ApiService.class);

    }

    // Metodo que busca as vagas.

    // Recebemos um Callback para avisar quando
    // a resposta chegar.

    public void buscarVagas(
            Callback<List<Vagas>> callback) {


        // Faz a chamada para vagas.php

        Call<List<Vagas>> chamada =
                api.buscarVagas();

        // enqueue executa a chamada em segundo plano.
        // O aplicativo não trava enquanto espera a resposta.
        chamada.enqueue(callback);

    }

}

