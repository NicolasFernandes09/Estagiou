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

public class MainActivity extends AppCompatActivity {
    EditText edUsuario, edSenha, edEmail;
    Button btLogin, btCriarConta;
    DBHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        edUsuario = (EditText)findViewById(R.id.edtUsuario);
        edEmail = (EditText)findViewById(R.id.edtEmail);
        edSenha = (EditText)findViewById(R.id.edtSenha);
        btLogin = (Button)findViewById(R.id.btnEntrar);
        btCriarConta = (Button)findViewById(R.id.btnConta);

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

                if(username.isEmpty()){
                    Toast.makeText(MainActivity.this,"Usuário não inserido.",Toast.LENGTH_SHORT).show();
                }else if(email.isEmpty()){
                    Toast.makeText(MainActivity.this,"E-mail não inserido.",Toast.LENGTH_SHORT).show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(MainActivity.this,"Formato de e-mail inválido.",Toast.LENGTH_SHORT).show();
                }else if(password.isEmpty()){
                    Toast.makeText(MainActivity.this,"Senha não inserida.",Toast.LENGTH_SHORT).show();
                }else{
                    String res = db.validarLogin(username, password);
                    if(res.equals("OK")){
                        Toast.makeText(MainActivity.this,
                                "Login efetuado com sucesso!",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }else{
                        Toast.makeText(MainActivity.this,"Dados de Login Incorretos!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}