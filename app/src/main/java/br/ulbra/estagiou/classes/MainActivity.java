package br.ulbra.estagiou.classes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.classes.api.RetrofitClient;
import br.ulbra.estagiou.classes.api.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edUsuario, edSenha, edEmail;
    Button btLogin, btCriarConta;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = RetrofitClient
                .getRetrofit()
                .create(ApiService.class);

        edUsuario = findViewById(R.id.edtUsuario);
        edEmail = findViewById(R.id.edtEmail);
        edSenha = findViewById(R.id.edtSenha);

        btLogin = findViewById(R.id.btnEntrar);
        btCriarConta = findViewById(R.id.btnConta);

        btCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
                startActivity(intent);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = edUsuario.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String password = edSenha.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Usuário não inserido.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "E-mail não inserido.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this,
                            "Formato de e-mail inválido.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Senha não inserida.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                api.login("login", username, email, password)
                        .enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {

                                if (response.isSuccessful() && response.body() != null) {

                                    try {

                                        String resposta = response.body().string();
                                        resposta = resposta.trim();

                                        Toast.makeText(MainActivity.this,
                                                resposta,
                                                Toast.LENGTH_LONG).show();

                                        // Se o PHP retornar sucesso
                                        if (resposta.toLowerCase().contains("sucesso")
                                                || resposta.toLowerCase().contains("ok")) {


                                        }

                                    } catch (Exception e) {

                                        Toast.makeText(MainActivity.this,
                                                e.getMessage(),
                                                Toast.LENGTH_LONG).show();

                                    }

                                } else {

                                    Toast.makeText(MainActivity.this,
                                            "Erro no servidor.",
                                            Toast.LENGTH_LONG).show();

                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call,
                                                  Throwable t) {

                                Toast.makeText(MainActivity.this,
                                        "Erro: " + t.getMessage(),
                                        Toast.LENGTH_LONG).show();

                            }

                        });

            }
        });

    }
}