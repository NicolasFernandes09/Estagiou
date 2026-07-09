package br.ulbra.estagiou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import br.ulbra.estagiou.adapter.VagasAdapter;
import br.ulbra.estagiou.funcoes.VagaController;
import br.ulbra.estagiou.model.Vagas;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {


    private VagaController controller;
    private RecyclerView recycler;
    private VagasAdapter adapter;
    private ArrayList<Vagas> lista = new ArrayList<>();
    Button testebt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = new VagaController();
        carregarVagas();
    }

    // ⬇️ FICA AQUI FORA DO onCreate()
    private void carregarVagas() {

        controller.listarVagas(new Callback<List<Vagas>>() {
            @Override
            public void onResponse(Call<List<Vagas>> call, Response<List<Vagas>> response) {
                if (response.isSuccessful()) {
                    lista.clear();
                    lista.addAll(response.body());
                    Log.d("VAGAS", "Total: " + lista.size());
                }
            }
            @Override
            public void onFailure(Call<List<Vagas>> call, Throwable t) {
                Log.e("ERRO", t.getMessage());
            }
        });
    }
}