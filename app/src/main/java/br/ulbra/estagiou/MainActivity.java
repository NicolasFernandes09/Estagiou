package br.ulbra.estagiou;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;


import android.os.Bundle;
import android.util.Log;

import java.util.List;

import br.ulbra.estagiou.funcoes.VagaController;
import br.ulbra.estagiou.repository.VagaRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import br.ulbra.estagiou.model.Vagas;



public class MainActivity extends AppCompatActivity {

    // Declara o Controller
    private VagaController controller;

    Button testebt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cria o Controller
        controller = new VagaController();

        // Chama o metodo que busca as vagas
        carregarVagas();

    }
}