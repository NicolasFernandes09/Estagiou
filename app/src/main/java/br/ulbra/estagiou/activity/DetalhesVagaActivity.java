package br.ulbra.estagiou.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.controller.VagaController;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.FavoritosStore;
import br.ulbra.estagiou.util.AssistenteHelper;
import br.ulbra.estagiou.util.BottomNavHelper;
import br.ulbra.estagiou.util.TelaHelper;

public class DetalhesVagaActivity extends AppCompatActivity {
    private static final int REQUEST_CURRICULO = 40;
    private VagaDados vagaAtual;
    private VagaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_vaga);
        TelaHelper.preencherPainel(this, R.id.cardDetalhesVaga, 36);

        controller = new VagaController();
        String vagaId = getIntent().getStringExtra(VagaDados.EXTRA_VAGA_ID);
        vagaAtual = controller.buscarVagaPorId(vagaId);

        preencherDados();
        configurarAcoes();
        pintarMenu();
        atualizarDadosDaApi(vagaId);
        AssistenteHelper.mostrarSePreciso(this, "detalhes",
                "Detalhes da vaga",
                "Nesta tela você confere salário, contato, prazo e pode enviar seu currículo em PDF.");
    }

    private void atualizarDadosDaApi(String vagaId) {
        if (controller.apiCarregada()) {
            return;
        }

        controller.carregarVagas(this, new VagaController.CarregamentoCallback() {
            @Override
            public void onResult(List<VagaDados> vagas) {
                VagaDados vaga = controller.encontrarVagaPorId(vagaId);
                if (vaga != null) {
                    vagaAtual = vaga;
                    preencherDados();
                }
            }

            @Override
            public void onError(String mensagem) {
            }
        });
    }

    private void preencherDados() {
        setTexto(R.id.txtSiglaEmpresaDetalhes, vagaAtual.sigla);
        setTexto(R.id.txtEmpresaDetalhes, vagaAtual.empresa);
        setTexto(R.id.txtCargoDetalhes, vagaAtual.titulo);
        setTexto(R.id.txtTipoDetalhes, vagaAtual.tipo);
        setTexto(R.id.txtCidadeDetalhes, vagaAtual.cidade);
        setTexto(R.id.txtDescricaoVaga, vagaAtual.descricao);
        setTexto(R.id.txtSalarioVaga, vagaAtual.salario);
        setTexto(R.id.txtCidadeContato, "Cidade: " + vagaAtual.cidade);
        setTexto(R.id.txtEmailContato, vagaAtual.contato);
        setTexto(R.id.txtTelefoneContato, vagaAtual.telefone);
        setTexto(R.id.txtDataLimite, vagaAtual.dataLimite);
        setTexto(R.id.txtCandidatura, vagaAtual.candidatura);
        setTexto(R.id.txtResumoEnvioCurriculo, "Para: " + emailContato() + "\nAssunto: " + assuntoEmail());

        CheckBox favorito = findViewById(R.id.checkFavoritoDetalhes);
        if (favorito != null) {
            favorito.setOnCheckedChangeListener(null);
            favorito.setChecked(FavoritosStore.isFavorita(this, vagaAtual.id));
            favorito.setOnCheckedChangeListener((buttonView, isChecked) ->
                    FavoritosStore.setFavorita(DetalhesVagaActivity.this, vagaAtual.id, isChecked));
        }
    }

    private void setTexto(int id, String texto) {
        TextView textView = findViewById(id);
        if (textView != null) {
            textView.setText(texto);
        }
    }

    private void configurarAcoes() {
        View voltar = findViewById(R.id.btnVoltarDetalhes);
        if (voltar != null) {
            voltar.setOnClickListener(v -> finish());
        }

        View enviarCurriculo = findViewById(R.id.btnEnviarCurriculo);
        if (enviarCurriculo != null) {
            enviarCurriculo.setOnClickListener(v -> selecionarCurriculo());
        }

        abrirVagas(R.id.navInicioFixoDetalhes);
        abrirVagas(R.id.imgInicioFixoDetalhes);
        abrirVagas(R.id.txtInicioFixoDetalhes);

        abrirFavoritos(R.id.navFavoritosFixoDetalhes);
        abrirFavoritos(R.id.imgFavoritosFixoDetalhes);
        abrirFavoritos(R.id.txtFavoritosFixoDetalhes);

        abrirPerfil(R.id.navPerfilFixoDetalhes);
        abrirPerfil(R.id.imgPerfilFixoDetalhes);
        abrirPerfil(R.id.txtPerfilFixoDetalhes);
    }

    private void abrirVagas(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(DetalhesVagaActivity.this, VagasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }
    }

    private void abrirFavoritos(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> startActivity(new Intent(DetalhesVagaActivity.this, FavoritosActivity.class)));
        }
    }

    private void abrirPerfil(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> startActivity(new Intent(DetalhesVagaActivity.this, PerfilActivity.class)));
        }
    }

    private void selecionarCurriculo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CURRICULO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CURRICULO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            enviarCurriculoPorEmail(data.getData());
        }
    }

    private void enviarCurriculoPorEmail(Uri curriculoUri) {
        Intent intent = criarIntentEmail(curriculoUri);
        ResolveInfo appEmail = buscarAppEmail(intent);

        if (appEmail != null) {
            intent.setPackage(appEmail.activityInfo.packageName);
            try {
                startActivity(intent);
                esconderTutorialEmail();
                return;
            } catch (Exception ignored) {
            }
        }

        mostrarTutorialEmail(curriculoUri);
    }

    private String emailContato() {
        String contato = vagaAtual.contato.replace("Contato:", "").trim();
        if (contato.contains("@")) {
            return contato;
        }
        return "";
    }

    private Intent criarIntentEmail(Uri curriculoUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailContato()});
        intent.putExtra(Intent.EXTRA_SUBJECT, assuntoEmail());
        intent.putExtra(Intent.EXTRA_TEXT,
                "Olá, segue meu currículo para a vaga " + vagaAtual.titulo + ".");
        intent.putExtra(Intent.EXTRA_STREAM, curriculoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private ResolveInfo buscarAppEmail(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo app : apps) {
            String pacote = app.activityInfo.packageName.toLowerCase(Locale.ROOT);
            String nome = app.activityInfo.name.toLowerCase(Locale.ROOT);
            if (pacote.contains("gmail") || pacote.contains("mail") || pacote.contains("outlook")
                    || nome.contains("gmail") || nome.contains("mail") || nome.contains("outlook")) {
                return app;
            }
        }

        return null;
    }

    private String assuntoEmail() {
        return "Candidatura - " + vagaAtual.titulo;
    }

    private void mostrarTutorialEmail(Uri curriculoUri) {
        setTexto(R.id.txtCurriculoAnexado, "PDF anexado: " + nomeArquivo(curriculoUri));
        setTexto(R.id.txtDadosTutorialEmail, "Para: " + emailContato() + "\nAssunto: " + assuntoEmail());

        View tutorial = findViewById(R.id.secaoTutorialEmail);
        if (tutorial != null) {
            tutorial.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "PDF anexado. Siga o passo a passo na tela.", Toast.LENGTH_LONG).show();
    }

    private void esconderTutorialEmail() {
        View tutorial = findViewById(R.id.secaoTutorialEmail);
        if (tutorial != null) {
            tutorial.setVisibility(View.GONE);
        }
    }

    private String nomeArquivo(Uri uri) {
        String caminho = uri.getLastPathSegment();
        if (caminho == null || caminho.equals("")) {
            return "currículo.pdf";
        }

        int barra = caminho.lastIndexOf('/');
        if (barra >= 0 && barra + 1 < caminho.length()) {
            return caminho.substring(barra + 1);
        }

        return caminho;
    }

    private void pintarMenu() {
        BottomNavHelper.pintarItem(this, R.id.imgInicioFixoDetalhes, R.id.txtInicioFixoDetalhes, true);
        BottomNavHelper.pintarItem(this, R.id.imgFavoritosFixoDetalhes, R.id.txtFavoritosFixoDetalhes, false);
        BottomNavHelper.pintarItem(this, R.id.imgPerfilFixoDetalhes, R.id.txtPerfilFixoDetalhes, false);
    }
}
