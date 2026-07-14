package br.ulbra.estagiou.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.api.UsuarioApiClient;
import br.ulbra.estagiou.repository.UsuarioStore;
import br.ulbra.estagiou.util.TelaHelper;

public class RegistrarActivity extends AppCompatActivity {
    private static final int REQUEST_FOTO = 10;
    private static final int LIMITE_DESCRICAO = 450;

    EditText edNome, edUsuario, edGmail, edPas1, edPas2, edDescricao, edDescricaoPessoal;
    Button btSalvar, btVoltarLogin, btSelecionarFoto;
    ImageView imgFotoCadastro;
    UsuarioApiClient api;
    Uri fotoSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        TelaHelper.preencherPainel(this, R.id.registerCard, 26);

        api = new UsuarioApiClient();
        edNome = (EditText) findViewById(R.id.edtNome);
        edUsuario = (EditText) findViewById(R.id.edtUsuarioCadastro);
        edGmail = (EditText) findViewById(R.id.edtGmail);
        edDescricao = (EditText) findViewById(R.id.edtDescricaoCadastro);
        edDescricaoPessoal = (EditText) findViewById(R.id.edtDescricaoPessoalCadastro);
        edPas1 = (EditText) findViewById(R.id.edtPass1);
        edPas2 = (EditText) findViewById(R.id.edtPass2);
        btSalvar = (Button) findViewById(R.id.btnRegistrar);
        btVoltarLogin = (Button) findViewById(R.id.btnEntrar2);
        btSelecionarFoto = (Button) findViewById(R.id.btnSelecionarFotoCadastro);
        imgFotoCadastro = (ImageView) findViewById(R.id.imgFotoCadastro);
        configurarContadorDescricao(edDescricao, (TextView) findViewById(R.id.txtRestanteDescricaoCadastro));
        configurarContadorDescricao(edDescricaoPessoal, (TextView) findViewById(R.id.txtRestanteDescricaoPessoalCadastro));

        btSelecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarFoto();
            }
        });

        btVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tentarRegistrar();
            }
        });
    }

    private void tentarRegistrar() {
        String nomeCompleto = edNome.getText().toString().trim();
        String userName = edUsuario.getText().toString().trim();
        String email = edGmail.getText().toString().trim();
        String descricaoProfissional = edDescricao.getText().toString().trim();
        String descricaoPessoal = edDescricaoPessoal.getText().toString().trim();
        String pas1 = edPas1.getText().toString();
        String pas2 = edPas2.getText().toString();
        String foto = fotoSelecionada == null ? "" : fotoSelecionada.toString();

        if (nomeCompleto.equals("")) {
            Toast.makeText(RegistrarActivity.this, "Insira o nome completo", Toast.LENGTH_SHORT).show();
        } else if (userName.equals("")) {
            Toast.makeText(RegistrarActivity.this, "Insira o usuário", Toast.LENGTH_SHORT).show();
        } else if (userName.length() < 3 || userName.length() > 20) {
            Toast.makeText(RegistrarActivity.this, "Usuário deve ter entre 3 e 20 caracteres", Toast.LENGTH_LONG).show();
        } else if (!usuarioValido(userName)) {
            Toast.makeText(RegistrarActivity.this, "Usuário pode ter apenas letras, acentos, números e underline", Toast.LENGTH_LONG).show();
        } else if (email.equals("")) {
            Toast.makeText(RegistrarActivity.this, "Insira o e-mail", Toast.LENGTH_SHORT).show();
        } else if (!emailValido(email)) {
            Toast.makeText(RegistrarActivity.this, "Insira um e-mail real e válido", Toast.LENGTH_SHORT).show();
        } else if (pas1.equals("") || pas2.equals("")) {
            Toast.makeText(RegistrarActivity.this, "Insira a senha do usuário", Toast.LENGTH_SHORT).show();
        } else if (pas1.length() < 6) {
            Toast.makeText(RegistrarActivity.this, "Senha mínima de 6 caracteres", Toast.LENGTH_SHORT).show();
        } else if (!pas1.equals(pas2)) {
            Toast.makeText(RegistrarActivity.this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
        } else {
            registrarNaApi(nomeCompleto, userName, email, pas1, descricaoProfissional, descricaoPessoal, foto);
        }
    }

    private void registrarNaApi(String nomeCompleto, String userName, String email, String senha,
                                String descricaoProfissional, String descricaoPessoal, String foto) {
        btSalvar.setEnabled(false);

        api.registrar(this, nomeCompleto, userName, email, senha, descricaoProfissional, descricaoPessoal, foto,
                new UsuarioApiClient.Callback() {
                    @Override
                    public void onSuccess(String mensagem) {
                        UsuarioStore.salvarUsuario(RegistrarActivity.this, nomeCompleto, userName, email,
                                descricaoProfissional, descricaoPessoal, foto);
                        ativarAssistenteInicial(userName);
                        Toast.makeText(RegistrarActivity.this, "Registro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String mensagem) {
                        btSalvar.setEnabled(true);
                        Toast.makeText(RegistrarActivity.this, mensagem, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fotoSelecionada = data.getData();
            imgFotoCadastro.setPadding(0, 0, 0, 0);
            imgFotoCadastro.setImageURI(fotoSelecionada);

            try {
                getContentResolver().takePersistableUriPermission(
                        fotoSelecionada,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {
            }
        }
    }

    private boolean usuarioValido(String userName) {
        return userName.matches("^[A-Za-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u00FF0-9_]+$");
    }

    private void configurarContadorDescricao(EditText editText, TextView contador) {
        if (editText == null || contador == null) {
            return;
        }

        atualizarContadorDescricao(editText, contador);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarContadorDescricao(editText, contador);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void atualizarContadorDescricao(EditText editText, TextView contador) {
        int restante = LIMITE_DESCRICAO - editText.getText().length();
        contador.setText(restante + " caracteres restantes");
    }

    private boolean emailValido(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        String[] partes = email.split("@");
        if (partes.length != 2) {
            return false;
        }

        String dominio = partes[1];
        return dominio.contains(".") && !dominio.startsWith(".") && !dominio.endsWith(".");
    }

    private void ativarAssistenteInicial(String userName) {
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .putBoolean("assistente_ativo_" + userName, true)
                .apply();
    }
}
