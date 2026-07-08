package br.ulbra.estagiou.classes;

import android.os.Bundle;
import android.util.Patterns;
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

public class RegistrarActivity extends AppCompatActivity {

    EditText edNome, edGmail, edPas1, edPas2;
    Button btSalvar, btVoltarLogin;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        api = RetrofitClient
                .getRetrofit()
                .create(ApiService.class);

        edNome = findViewById(R.id.edtNome);
        edGmail = findViewById(R.id.edtGmail);
        edPas1 = findViewById(R.id.edtPass1);
        edPas2 = findViewById(R.id.edtPass2);

        btSalvar = findViewById(R.id.btnRegistrar);
        btVoltarLogin = findViewById(R.id.btnEntrar2);

        btVoltarLogin.setOnClickListener(view -> finish());

        btSalvar.setOnClickListener(view -> {

            String userName = edNome.getText().toString().trim();
            String email = edGmail.getText().toString().trim();
            String pas1 = edPas1.getText().toString().trim();
            String pas2 = edPas2.getText().toString().trim();

            String usuarioRegex = "^[a-zA-Z0-9_]{3,20}$";

            if (userName.isEmpty()) {
                Toast.makeText(this, "Insira o nome de usuário.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!userName.matches(usuarioRegex)) {
                Toast.makeText(this, "Usuário deve ter entre 3 e 20 caracteres.", Toast.LENGTH_LONG).show();
                return;
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Insira o e-mail.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Insira um e-mail válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pas1.isEmpty() || pas2.isEmpty()) {
                Toast.makeText(this, "Preencha os campos de senha.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pas1.length() < 6) {
                Toast.makeText(this, "A senha deve conter no mínimo 6 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pas1.equals(pas2)) {
                Toast.makeText(this, "As senhas não correspondem.", Toast.LENGTH_SHORT).show();
                return;
            }

            api.registrar("registrar", userName, email, pas1)
                    .enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {

                            if (response.isSuccessful()) {

                                try {

                                    String resposta = response.body().string();

                                    Toast.makeText(
                                            RegistrarActivity.this,
                                            resposta.trim(),
                                            Toast.LENGTH_LONG).show();

                                    if (resposta.trim().equalsIgnoreCase("sucesso")) {
                                        Toast.makeText(
                                                RegistrarActivity.this,
                                                "Cadastro realizado!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        finish();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {

                                Toast.makeText(RegistrarActivity.this,
                                        "Erro no servidor.",
                                        Toast.LENGTH_LONG).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                            Toast.makeText(RegistrarActivity.this,
                                    "Erro: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }

                    });

        });

    }
}