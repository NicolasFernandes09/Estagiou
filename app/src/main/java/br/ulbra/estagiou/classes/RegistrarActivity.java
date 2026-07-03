package br.ulbra.estagiou.classes;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;

public class RegistrarActivity extends AppCompatActivity {
    EditText edNome, edGmail, edPas1, edPas2;
    Button btSalvar, btVoltarLogin;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        db = new DBHelper(this);

        edNome = (EditText)findViewById(R.id.edtNome);
        edGmail = (EditText)findViewById(R.id.edtGmail);
        edPas1 = (EditText)findViewById(R.id.edtPass1);
        edPas2 = (EditText)findViewById(R.id.edtPass2);
        btSalvar = (Button)findViewById(R.id.btnRegistrar);
        btVoltarLogin = (Button)findViewById(R.id.btnEntrar2);

        btVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edNome.getText().toString().trim();
                String email = edGmail.getText().toString().trim();
                String pas1 = edPas1.getText().toString().trim();
                String pas2 = edPas2.getText().toString().trim();

                // Expressão regular: Permite apenas letras e números, de 3 a 20 caracteres
                String usuarioRegex = "^[a-zA-Z0-9_]{3,20}$";

                if (userName.isEmpty()) {
                    Toast.makeText(RegistrarActivity.this, "Insira o nome de usuário.", Toast.LENGTH_SHORT).show();
                } else if(!userName.matches(usuarioRegex)){
                    Toast.makeText(RegistrarActivity.this, "Usuário deve ter entre 3 e 20 caracteres (sem símbolos).", Toast.LENGTH_LONG).show();
                } else if(email.isEmpty()){
                    Toast.makeText(RegistrarActivity.this, "Insira o e-mail.", Toast.LENGTH_SHORT).show();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegistrarActivity.this, "Insira um e-mail válido.", Toast.LENGTH_SHORT).show();
                } else if (pas1.isEmpty() || pas2.isEmpty()){
                    Toast.makeText(RegistrarActivity.this, "Preencha os campos de senha.", Toast.LENGTH_SHORT).show();
                } else if(pas1.length() < 6){
                    // Bloqueia senhas muito curtas
                    Toast.makeText(RegistrarActivity.this, "A senha deve conter no mínimo 6 caracteres.", Toast.LENGTH_SHORT).show();
                } else if(!pas1.equals(pas2)){
                    Toast.makeText(RegistrarActivity.this, "As senhas não correspondem.", Toast.LENGTH_SHORT).show();
                } else {
                    // Impede o cadastro se o nome de usuário já existir no banco
                    if(db.usuarioExiste(userName)) {
                        Toast.makeText(RegistrarActivity.this, "Este nome de usuário já está em uso.", Toast.LENGTH_SHORT).show();
                    } else {
                        long res = db.criarUtilizador(userName, email, pas1);
                        if(res > 0){
                            Toast.makeText(RegistrarActivity.this, "Registro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegistrarActivity.this, "Erro ao salvar no banco de dados.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}