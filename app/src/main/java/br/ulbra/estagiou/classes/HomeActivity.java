package br.ulbra.estagiou.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;

public class HomeActivity extends AppCompatActivity {

    TextView txtBoasVindas;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtBoasVindas = findViewById(R.id.txtBoasVindas);
        btnLogout = findViewById(R.id.btnLogout);

        // Recupera o nome do utilizador guardado de forma segura na sessão
        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String username = preferences.getString("logged_user", "Utilizador");

        txtBoasVindas.setText("Bem-vindo de volta, " + username + "!");

        // Ação de encerrar sessão com segurança
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpa as preferências da sessão
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear(); // Apaga todos os dados guardados da sessão
                editor.apply();

                // Redireciona de volta para a tela de login
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Garante que ele não consegue voltar com o botão físico de voltar do telemóvel
            }
        });
    }
}