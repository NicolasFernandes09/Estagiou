package br.ulbra.estagiou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.ulbra.estagiou.adapter.VagasAdapter;
import br.ulbra.estagiou.funcoes.VagaController;
import br.ulbra.estagiou.model.Vagas;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VagasAdapter adapter;
    private VagaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerVagas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        controller = new VagaController();

        controller.carregarVagas(new Callback<List<Vagas>>() {
            @Override
            public void onResponse(Call<List<Vagas>> call, Response<List<Vagas>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    adapter = new VagasAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Vagas>> call, Throwable t) {

                Toast.makeText(MainActivity.this,
                        "Erro: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}