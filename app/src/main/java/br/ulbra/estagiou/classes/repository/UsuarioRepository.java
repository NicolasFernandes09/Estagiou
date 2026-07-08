package br.ulbra.estagiou.classes.repository;

import br.ulbra.estagiou.classes.api.ApiService;
import br.ulbra.estagiou.classes.api.RetrofitClient;

public class UsuarioRepository {
    private ApiService api;

    public UsuarioRepository() {
        api = RetrofitClient.getRetrofit().create(ApiService.class);

    }
    public void
}
