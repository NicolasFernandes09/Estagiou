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


    // CREATE - Inserir uma vaga

    public void inserirVagas(Vagas vaga, Callback<Void> callback) {
        //Chama o metodo da API responsavel por iserir
        Call<Void> chamada = api.inserirVagas(vaga);

        // Executa a requisição em segundo plano
        chamada.enqueue(callback);
    }


    // READ - Buscar todas as vagas

    public void buscarVagas(
            Callback<List<Vagas>> callback) {
        // Faz a chamada para vagas.php
        Call<List<Vagas>> chamada = api.buscarVagas();
        // enqueue executa a chamada em segundo plano.
        // O aplicativo não trava enquanto espera a resposta.
        chamada.enqueue(callback);
    }


    // UPDATE - Atualizar uma vaga

    public void atualizarVagas(
            Vagas vaga,
            Callback<Void> callback) {

        // Envia os novos dados da vaga para o servidor
        Call<Void> chamada =
                api.atualizarVagas(vaga.getVagasId(), vaga);

        // Executa a atualização em segundo plano
        chamada.enqueue(callback);
    }

    // DELETE - Excluir uma vaga
    public void excluirVagas(
            int id,
            Callback<Void> callback) {

        // Solicita ao servidor a exclusão
        // da vaga com o ID informado
        Call<Void> chamada =
                api.excluirVagas(id);

        // Executa a exclusão em segundo plano
        chamada.enqueue(callback);
    }
}

