package br.ulbra.estagiou.classes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;

public class RegistrarActivity extends AppCompatActivity {
    EditText edNome, edGmail, edPas1, edPas2;
    Button btSalvar, btVoltarLogin; // Adicionado btVoltarLogin aqui
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

        // Mapeando o botão "Entrar" do XML de registro
        btVoltarLogin = (Button)findViewById(R.id.btnEntrar2);

        // Configurando o clique para voltar para a tela de Login
        btVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Destrói a RegistrarActivity e volta para a MainActivity
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edNome.getText().toString();
                String email = edGmail.getText().toString();
                String pas1 = edPas1.getText().toString();
                String pas2 = edPas2.getText().toString();
                if (userName.equals("")) {
                    Toast.makeText(RegistrarActivity.this, "Insira o LOGIN DO USUÁRIO", Toast.LENGTH_SHORT).show();
                } else if (pas1.equals("") || pas2.equals("")){
                    Toast.makeText(RegistrarActivity.this, "Insira a SENHA DO USUÁRIO", Toast.LENGTH_SHORT).show();
                }else if(!pas1.equals(pas2)){
                    Toast.makeText(RegistrarActivity.this, "As senhas não correspondem ao login do usuário", Toast.LENGTH_SHORT).show();
                }else{
                    long res = db.criarUtilizador(userName, email, pas1);
                    if(res>0){
                        Toast.makeText(RegistrarActivity.this, "Registro OK", Toast.LENGTH_SHORT).show();
                        finish(); // Opcional: fecha a tela de cadastro após registrar com sucesso para ele logar
                    }else{
                        Toast.makeText(RegistrarActivity.this, "Senha inválida!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}