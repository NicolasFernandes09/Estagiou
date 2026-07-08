package br.ulbra.estagiou;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;


import android.os.Bundle;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import br.ulbra.estagiou.model.Vagas;
import br.ulbra.estagiou.repository.VagaRepository;


public class MainActivity extends AppCompatActivity {

    // Declara o objeto que vai buscar as vagas
    private VagaRepository repository;

    Button testebt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cria o Repository
        repository = new VagaRepository();
        // Chama a API
        repository.buscarVagas(
                new Callback<List<Vagas>>() {
                    // Executa quando a API responde corretamente
                    @Override
                    public void onResponse(
                            Call<List<Vagas>> call,
                            Response<List<Vagas>> response) {
                        // Verifica se a resposta veio OK
                        if (response.isSuccessful()) {
                            // Pega a lista de vagas recebida
                            List<Vagas> vagas =
                                    response.body();
                            // Percorre todas as vagas
                            for (Vagas vaga : vagas) {
                                // Mostra no Logcat

                                Log.d(
                                        "API",
                                        "Empresa: " + vaga.getTitulo()
                                                + " Descrição: "
                                                + vaga.getDescricao()
                                );
                            }
                        }
                    }

                    // Executa caso dê erro de conexão
                    @Override
                    public void onFailure(
                            Call<List<Vagas>> call,
                            Throwable t) {
                        Log.e(
                                "ERRO API",
                                t.getMessage()
                        );
                    }
                });
    }
}