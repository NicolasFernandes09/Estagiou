package br.ulbra.estagiou.classes;

import android.content.Intent; // IMPORTANTE: Adicione esta linha se o Android Studio não importar sozinho
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;

public class MainActivity extends AppCompatActivity {
    EditText edUsuario, edSenha, edEmail;
    Button btLogin, btCriarConta; // Adicionado o botão btCriarConta aqui
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

        // Mapeando o botão "Criar conta" do seu XML
        btCriarConta = (Button)findViewById(R.id.btnConta);

        // Configurando o clique para ir para a tela de Registro
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
                String username=edUsuario.getText().toString();
                String email=edEmail.getText().toString();
                String password=edSenha.getText().toString();
                if(username.equals("")){
                    Toast.makeText(MainActivity.this,"Usuario não inserido, tente novamente",Toast.LENGTH_SHORT).show();
                }else if(password.equals("")){
                    Toast.makeText(MainActivity.this,"Senha não inserida, tente novamente",Toast.LENGTH_SHORT).show();
                }else if(email.equals("")){
                    Toast.makeText(MainActivity.this,"Email não inserido, tente novamente",Toast.LENGTH_SHORT).show();
                }else{
                    String res = db.validarLogin(username,password);
                    if(res.equals("OK")){
                        Toast.makeText(MainActivity.this,"Login OK !!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"Dados de Login Incorretos!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}