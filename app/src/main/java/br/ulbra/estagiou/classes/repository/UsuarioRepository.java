package br.ulbra.estagiou.classes.repository;

import br.ulbra.estagiou.classes.api.ApiService;
import br.ulbra.estagiou.classes.api.RetrofitClient;
import java.util.List;
import br.ulbra.estagiou.classes.model.Usuarios;
import retrofit2.Call;
import retrofit2.Callback;

public class UsuarioRepository {
    private ApiService api;

    public UsuarioRepository() {
        api = RetrofitClient.getRetrofit().create(ApiService.class);

    }
    public void inserirUsuarios(Usuarios usuario, Callback<Void> callback) {
        //Chama o metodo da API responsavel por iserir
        Call<Void> chamada = api.inserirUsuarios(usuario);

        // Executa a requisição em segundo plano
        chamada.enqueue(callback);
    }
    public void buscarUsuarios(
            Callback<List<Usuarios>> callback) {
        Call<List<Usuarios>> chamada =
                api.buscarUsuarios();
        chamada.enqueue(callback);
    }

    public void atualizarUsuarios(
            Usuarios usuario,
            Callback<Void> callback) {
        Call<Void> chamada =
                api.atualizarUsuarios(usuario.getUsuarioId(), usuario);
        chamada.enqueue(callback);
    }
    public void excluirUsuarios(
            int id,
            Callback<Void> callback) {
        Call<Void> chamada =
                api.excluirUsuarios(id);
        chamada.enqueue(callback);
    }
}

