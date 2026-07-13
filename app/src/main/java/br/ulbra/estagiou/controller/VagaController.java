package br.ulbra.estagiou.controller;

import android.content.Context;

import java.util.List;

import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.model.Vagas;
import br.ulbra.estagiou.repository.VagaRepository;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public class VagaController {
    public interface CarregamentoCallback {
        void onResult(List<VagaDados> vagas);

        void onError(String mensagem);
    }

    private final VagaRepository repository;

    public VagaController() {
        repository = new VagaRepository();
    }

    public void carregarVagas(Context context, CarregamentoCallback callback) {
        executarCarregamento(context, false, callback);
    }

    public void recarregarVagas(Context context, CarregamentoCallback callback) {
        executarCarregamento(context, true, callback);
    }

    private void executarCarregamento(Context context, boolean forcar, CarregamentoCallback callback) {
        VagaRepository.Callback repositoryCallback = new VagaRepository.Callback() {
            @Override
            public void onResult(List<VagaDados> vagas) {
                callback.onResult(vagas);
            }

            @Override
            public void onError(String mensagem) {
                callback.onError(mensagem);
            }
        };

        if (forcar) {
            repository.recarregar(context, repositoryCallback);
        } else {
            repository.carregar(context, repositoryCallback);
        }
    }

    public List<VagaDados> listarVagas() {
        return repository.listar();
    }

    public VagaDados buscarVagaPorId(String id) {
        return repository.buscarPorId(id);
    }

    public VagaDados encontrarVagaPorId(String id) {
        return repository.encontrarPorId(id);
    }

    public boolean apiCarregada() {
        return repository.apiCarregada();
    }

    public void inserirVaga(Vagas vaga, Callback<ResponseBody> callback) {
        repository.inserirVaga(vaga, callback);
    }

    public void atualizarVaga(Vagas vaga, Callback<ResponseBody> callback) {
        repository.atualizarVaga(vaga, callback);
    }

    public void excluirVaga(int id, Callback<ResponseBody> callback) {
        repository.excluirVaga(id, callback);
    }
}
