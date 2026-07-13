package br.ulbra.estagiou.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.api.UsuarioApiClient;
import br.ulbra.estagiou.repository.UsuarioStore;
import br.ulbra.estagiou.repository.SessaoManager;
import br.ulbra.estagiou.util.AssistenteHelper;
import br.ulbra.estagiou.util.BottomNavHelper;
import br.ulbra.estagiou.util.TelaHelper;

public class PerfilActivity extends AppCompatActivity {
    private static final int REQUEST_FOTO_PERFIL = 30;
    private static final String SHARED_PREFS_NAME = "user_session";
    private static final String KEY_USERNAME = "logged_user";
    private static final int LIMITE_DESCRICAO = 450;

    String usuarioLogado = "";
    String nomeAtual = "";
    String emailAtual = "";
    String descricaoProfissionalAtual = "";
    String descricaoPessoalAtual = "";
    String fotoAtual = "";
    Uri fotoEdicao;
    ImageView imgFotoEditarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        TelaHelper.preencherPainel(this, R.id.cardPerfil, 36);

        preencherPerfil();
        configurarAcoes();
        pintarMenu();
        AssistenteHelper.mostrarSePreciso(this, "perfil",
                "Perfil",
                "Aqui você acompanha seus dados, adiciona foto, descrições e pode editar suas informações.");
    }

    private void preencherPerfil() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        usuarioLogado = preferences.getString(KEY_USERNAME, "usuario");

        UsuarioStore.UsuarioDados dados = UsuarioStore.buscarUsuario(this, usuarioLogado);
        nomeAtual = dados.nome.equals("") ? usuarioLogado : dados.nome;
        emailAtual = dados.email;
        descricaoProfissionalAtual = dados.descricaoProfissional;
        descricaoPessoalAtual = dados.descricaoPessoal;
        fotoAtual = dados.foto;

        preencherTexto(R.id.txtNomePerfil, nomeAtual);
        preencherTexto(R.id.txtUsuarioPerfil, "@" + usuarioLogado);
        preencherTexto(R.id.txtEmailPerfil, emailAtual);
        preencherTexto(R.id.txtDescricaoPerfil,
                descricaoProfissionalAtual.equals("") ? "Sem descrição profissional cadastrada." : descricaoProfissionalAtual);
        preencherTexto(R.id.txtDescricaoPessoalPerfil,
                descricaoPessoalAtual.equals("") ? "Sem descrição pessoal cadastrada." : descricaoPessoalAtual);
        preencherFoto(fotoAtual);
    }

    private void preencherTexto(int id, String texto) {
        TextView textView = findViewById(id);
        if (textView != null) {
            textView.setText(texto);
        }
    }

    private void preencherFoto(String foto) {
        ImageView imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        preencherFotoEmImagem(imgFotoPerfil, foto);
    }

    private void preencherFotoEmImagem(ImageView imagem, String foto) {
        if (imagem == null || foto == null || foto.equals("")) {
            return;
        }

        try {
            imagem.setPadding(0, 0, 0, 0);
            imagem.setImageURI(Uri.parse(foto));
        } catch (Exception ignored) {
        }
    }

    private void configurarAcoes() {
        abrirVagas(R.id.navInicioFixoPerfil);
        abrirVagas(R.id.imgInicioFixoPerfil);
        abrirVagas(R.id.txtInicioFixoPerfil);

        abrirFavoritos(R.id.navFavoritosFixoPerfil);
        abrirFavoritos(R.id.imgFavoritosFixoPerfil);
        abrirFavoritos(R.id.txtFavoritosFixoPerfil);

        View btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        if (btnEditarPerfil != null) {
            btnEditarPerfil.setOnClickListener(v -> mostrarDialogEditarPerfil());
        }

        View btnSairPerfil = findViewById(R.id.btnSairPerfil);
        if (btnSairPerfil != null) {
            btnSairPerfil.setOnClickListener(v -> confirmarSaida());
        }
    }

    private void mostrarDialogEditarPerfil() {
        View view = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);
        EditText edtNome = view.findViewById(R.id.edtNomeEditarPerfil);
        EditText edtEmail = view.findViewById(R.id.edtEmailEditarPerfil);
        EditText edtDescricaoProfissional = view.findViewById(R.id.edtDescricaoProfissionalEditarPerfil);
        EditText edtDescricaoPessoal = view.findViewById(R.id.edtDescricaoPessoalEditarPerfil);
        Button btnSelecionarFoto = view.findViewById(R.id.btnSelecionarFotoEditarPerfil);
        imgFotoEditarPerfil = view.findViewById(R.id.imgFotoEditarPerfil);

        fotoEdicao = fotoAtual.equals("") ? null : Uri.parse(fotoAtual);
        edtNome.setText(nomeAtual);
        edtEmail.setText(emailAtual);
        edtDescricaoProfissional.setText(descricaoProfissionalAtual);
        edtDescricaoPessoal.setText(descricaoPessoalAtual);
        configurarContadorDescricao(edtDescricaoProfissional,
                view.findViewById(R.id.txtRestanteDescricaoProfissionalEditarPerfil));
        configurarContadorDescricao(edtDescricaoPessoal,
                view.findViewById(R.id.txtRestanteDescricaoPessoalEditarPerfil));
        preencherFotoEmImagem(imgFotoEditarPerfil, fotoAtual);

        btnSelecionarFoto.setOnClickListener(v -> selecionarFotoPerfil());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Editar perfil")
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salvar", null)
                .create();

        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String nome = edtNome.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String profissional = edtDescricaoProfissional.getText().toString().trim();
                    String pessoal = edtDescricaoPessoal.getText().toString().trim();
                    String foto = fotoEdicao == null ? "" : fotoEdicao.toString();

                    if (nome.equals("")) {
                        Toast.makeText(PerfilActivity.this, "Informe seu nome", Toast.LENGTH_SHORT).show();
                    } else if (email.equals("") || !emailValido(email)) {
                        Toast.makeText(PerfilActivity.this, "Informe um e-mail válido", Toast.LENGTH_SHORT).show();
                    } else {
                        confirmarAlteracao(dialog, nome, email, profissional, pessoal, foto);
                    }
                }));

        dialog.show();
    }

    private void confirmarAlteracao(AlertDialog dialog, String nome, String email,
                                    String profissional, String pessoal, String foto) {
        new AlertDialog.Builder(this)
                .setTitle("Alterar informações?")
                .setMessage("Deseja mesmo alterar as informações do perfil?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Alterar", (confirmDialog, which) -> {
                    UsuarioStore.atualizarPerfil(PerfilActivity.this, usuarioLogado, nome, email, profissional, pessoal, foto);
                    Toast.makeText(PerfilActivity.this, "Perfil atualizado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    preencherPerfil();
                })
                .show();
    }

    private void selecionarFotoPerfil() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_FOTO_PERFIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOTO_PERFIL && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fotoEdicao = data.getData();
            preencherFotoEmImagem(imgFotoEditarPerfil, fotoEdicao.toString());

            try {
                int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(fotoEdicao, flags);
            } catch (Exception ignored) {
            }
        }
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

    private void abrirVagas(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(PerfilActivity.this, VagasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }
    }

    private void abrirFavoritos(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> startActivity(new Intent(PerfilActivity.this, FavoritosActivity.class)));
        }
    }

    private void confirmarSaida() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da conta?")
                .setMessage("Deseja mesmo sair da conta?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sair", (dialog, which) -> sairDaConta())
                .show();
    }

    private void sairDaConta() {
        new UsuarioApiClient().logout(this, new UsuarioApiClient.Callback() {
            @Override
            public void onSuccess(String mensagem) {
                finalizarSaida();
            }

            @Override
            public void onError(String mensagem) {
                finalizarSaida();
            }
        });
    }

    private void finalizarSaida() {
        SessaoManager.limpar(this);

        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void pintarMenu() {
        BottomNavHelper.pintarItem(this, R.id.imgInicioFixoPerfil, R.id.txtInicioFixoPerfil, false);
        BottomNavHelper.pintarItem(this, R.id.imgFavoritosFixoPerfil, R.id.txtFavoritosFixoPerfil, false);
        BottomNavHelper.pintarItem(this, R.id.imgPerfilFixoPerfil, R.id.txtPerfilFixoPerfil, true);
    }
}
