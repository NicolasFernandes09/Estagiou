package br.ulbra.estagiou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.api.UsuarioApiClient;
import br.ulbra.estagiou.repository.UsuarioStore;
import br.ulbra.estagiou.repository.SessaoManager;
import br.ulbra.estagiou.util.TelaHelper;

public class MainActivity extends AppCompatActivity {
    EditText edUsuario, edSenha, edEmail;
    Button btLogin, btCriarConta;
    UsuarioApiClient api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessaoManager.estaLogado(this)) {
            Intent intent = new Intent(MainActivity.this, VagasActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        TelaHelper.preencherPainel(this, R.id.loginCard, 26);

        api = new UsuarioApiClient();
        edUsuario = (EditText) findViewById(R.id.edtUsuario);
        edEmail = (EditText) findViewById(R.id.edtEmail);
        edSenha = (EditText) findViewById(R.id.edtSenha);
        btLogin = (Button) findViewById(R.id.btnEntrar);
        btCriarConta = (Button) findViewById(R.id.btnConta);

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
                String password = edSenha.getText().toString();

                if (username.equals("")) {
                    Toast.makeText(MainActivity.this, "Usuário não inserido, tente novamente", Toast.LENGTH_SHORT).show();
                } else if (email.equals("")) {
                    Toast.makeText(MainActivity.this, "E-mail não inserido, tente novamente", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this, "Formato de e-mail inválido", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(MainActivity.this, "Senha não inserida, tente novamente", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(MainActivity.this, "Senha mínima de 6 caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    fazerLogin(username, email, password);
                }
            }
        });
    }

    private void fazerLogin(String username, String email, String password) {
        btLogin.setEnabled(false);

        api.login(this, username, email, password, new UsuarioApiClient.Callback() {
            @Override
            public void onSuccess(String mensagem) {
                UsuarioStore.salvarUsuarioSeAusente(MainActivity.this, username, email);

                Toast.makeText(MainActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, VagasActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String mensagem) {
                btLogin.setEnabled(true);
                Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}
