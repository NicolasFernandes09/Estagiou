package br.ulbra.estagiou.classes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.ulbra.estagiou.R;

public class VagasActivity extends AppCompatActivity {
    private final List<CardVaga> cardsFixos = new ArrayList<>();
    private final List<View> cardsExtras = new ArrayList<>();

    private LinearLayout vagasContent;
    private TextView txtSemResultadosVagas;
    private EditText edtBuscaVaga;
    private String filtroAtual = "Todas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vagas);
        TelaHelper.preencherPainel(this, R.id.vagasCard, 36);

        inicializarViews();
        configurarAcoes();
        configurarBusca();
        configurarFiltros();
        pintarMenu();
        carregarVagas();
        AssistenteHelper.mostrarSePreciso(this, "vagas",
                "Tela de vagas",
                "Aqui você pode buscar oportunidades, usar filtros e tocar no coração para salvar uma vaga.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicarFiltroBusca();
    }

    private void inicializarViews() {
        vagasContent = findViewById(R.id.vagasContent);
        edtBuscaVaga = findViewById(R.id.edtBuscaVaga);
        txtSemResultadosVagas = findViewById(R.id.txtSemResultadosVagas);

        cardsFixos.add(new CardVaga(
                findViewById(R.id.cardVaga1),
                findViewById(R.id.txtSiglaEmpresa1),
                findViewById(R.id.txtEmpresaVaga1),
                findViewById(R.id.txtTituloVaga1),
                findViewById(R.id.txtCidadeVaga1),
                findViewById(R.id.txtTipoVaga1),
                findViewById(R.id.btnDetalhesVaga1),
                findViewById(R.id.checkFavoritoVaga1)));

        cardsFixos.add(new CardVaga(
                findViewById(R.id.cardVaga2),
                findViewById(R.id.txtSiglaEmpresa2),
                findViewById(R.id.txtEmpresaVaga2),
                findViewById(R.id.txtTituloVaga2),
                findViewById(R.id.txtCidadeVaga2),
                findViewById(R.id.txtTipoVaga2),
                findViewById(R.id.btnDetalhesVaga2),
                findViewById(R.id.checkFavoritoVaga2)));

        cardsFixos.add(new CardVaga(
                findViewById(R.id.cardVaga3),
                findViewById(R.id.txtSiglaEmpresa3),
                findViewById(R.id.txtEmpresaVaga3),
                findViewById(R.id.txtTituloVaga3),
                findViewById(R.id.txtCidadeVaga3),
                findViewById(R.id.txtTipoVaga3),
                findViewById(R.id.btnDetalhesVaga3),
                findViewById(R.id.checkFavoritoVaga3)));
    }

    private void configurarAcoes() {
        abrirFavoritos(R.id.navFavoritosFixoVagas);
        abrirFavoritos(R.id.imgFavoritosFixoVagas);
        abrirFavoritos(R.id.txtFavoritosFixoVagas);

        abrirPerfil(R.id.navPerfilFixoVagas);
        abrirPerfil(R.id.imgPerfilFixoVagas);
        abrirPerfil(R.id.txtPerfilFixoVagas);
    }

    private void configurarBusca() {
        if (edtBuscaVaga == null) {
            return;
        }

        edtBuscaVaga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltroBusca();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void configurarFiltros() {
        configurarFiltro(R.id.btnFiltroTodas, "Todas");
        configurarFiltro(R.id.btnFiltroEstagio, "Estágio");
        configurarFiltro(R.id.btnFiltroJovemAprendiz, "Jovem Aprendiz");
        configurarFiltro(R.id.btnFiltroFreelancer, "Freelancer");
        configurarFiltro(R.id.btnFiltroClt, "CLT");
    }

    private void carregarVagas() {
        VagasRepository.carregar(this, new VagasRepository.Callback() {
            @Override
            public void onResult(List<VagaDados> vagas) {
                aplicarFiltroBusca();
            }

            @Override
            public void onError(String mensagem) {
                Toast.makeText(VagasActivity.this, mensagem + ". Mostrando vagas salvas no app.", Toast.LENGTH_LONG).show();
                aplicarFiltroBusca();
            }
        });
    }

    private void configurarFiltro(int id, String filtro) {
        Button botao = findViewById(id);
        if (botao != null) {
            botao.setOnClickListener(v -> {
                filtroAtual = filtro;
                atualizarEstadoFiltros();
                aplicarFiltroBusca();
            });
        }
    }

    private void aplicarFiltroBusca() {
        if (vagasContent == null) {
            return;
        }

        limparCardsExtras();

        String busca = edtBuscaVaga == null ? "" : edtBuscaVaga.getText().toString();
        List<VagaDados> vagasFiltradas = new ArrayList<>();
        for (VagaDados vaga : VagasRepository.listar()) {
            if (vaga.combinaComFiltro(filtroAtual) && vaga.combinaComBusca(busca)) {
                vagasFiltradas.add(vaga);
            }
        }

        for (int i = 0; i < cardsFixos.size(); i++) {
            if (i < vagasFiltradas.size()) {
                preencherCard(cardsFixos.get(i), vagasFiltradas.get(i));
            } else {
                cardsFixos.get(i).card.setVisibility(View.GONE);
            }
        }

        for (int i = cardsFixos.size(); i < vagasFiltradas.size(); i++) {
            View cardExtra = getLayoutInflater().inflate(R.layout.item_vaga, vagasContent, false);
            preencherCard(new CardVaga(
                    cardExtra,
                    cardExtra.findViewById(R.id.txtSiglaEmpresaItem),
                    cardExtra.findViewById(R.id.txtEmpresaVagaItem),
                    cardExtra.findViewById(R.id.txtTituloVagaItem),
                    cardExtra.findViewById(R.id.txtCidadeVagaItem),
                    cardExtra.findViewById(R.id.txtTipoVagaItem),
                    cardExtra.findViewById(R.id.btnDetalhesVagaItem),
                    cardExtra.findViewById(R.id.checkFavoritoVagaItem)), vagasFiltradas.get(i));

            int posicaoMensagem = txtSemResultadosVagas == null ? vagasContent.getChildCount() : vagasContent.indexOfChild(txtSemResultadosVagas);
            vagasContent.addView(cardExtra, posicaoMensagem);
            cardsExtras.add(cardExtra);
        }

        if (txtSemResultadosVagas != null) {
            txtSemResultadosVagas.setVisibility(vagasFiltradas.isEmpty() ? View.VISIBLE : View.GONE);
        }

        atualizarEstadoFiltros();
    }

    private void preencherCard(CardVaga card, VagaDados vaga) {
        card.card.setVisibility(View.VISIBLE);
        card.sigla.setText(vaga.sigla);
        card.empresa.setText(vaga.empresa);
        card.titulo.setText(vaga.titulo);
        card.cidade.setText(vaga.cidade);
        card.tipo.setText(vaga.tipo);
        card.detalhes.setOnClickListener(v -> abrirDetalhes(vaga.id));

        card.favorito.setOnCheckedChangeListener(null);
        card.favorito.setChecked(FavoritosStore.isFavorita(this, vaga.id));
        card.favorito.setOnCheckedChangeListener((buttonView, isChecked) ->
                FavoritosStore.setFavorita(VagasActivity.this, vaga.id, isChecked));
    }

    private void limparCardsExtras() {
        for (View cardExtra : cardsExtras) {
            vagasContent.removeView(cardExtra);
        }
        cardsExtras.clear();
    }

    private void atualizarEstadoFiltros() {
        atualizarBotaoFiltro(R.id.btnFiltroTodas, "Todas");
        atualizarBotaoFiltro(R.id.btnFiltroEstagio, "Estágio");
        atualizarBotaoFiltro(R.id.btnFiltroJovemAprendiz, "Jovem Aprendiz");
        atualizarBotaoFiltro(R.id.btnFiltroFreelancer, "Freelancer");
        atualizarBotaoFiltro(R.id.btnFiltroClt, "CLT");
    }

    private void atualizarBotaoFiltro(int id, String filtro) {
        Button botao = findViewById(id);
        if (botao == null) {
            return;
        }

        boolean selecionado = filtroAtual.equals(filtro);
        botao.setBackgroundResource(selecionado ? R.drawable.bg_filter_selected : R.drawable.bg_filter_outline);
        botao.setTextColor(ContextCompat.getColor(this,
                selecionado ? R.color.white : R.color.estagiou_orange_corporate));
    }

    private void abrirDetalhes(String vagaId) {
        Intent intent = new Intent(VagasActivity.this, DetalhesVagaActivity.class);
        intent.putExtra(VagaDados.EXTRA_VAGA_ID, vagaId);
        startActivity(intent);
    }

    private void abrirFavoritos(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> startActivity(new Intent(VagasActivity.this, FavoritosActivity.class)));
        }
    }

    private void abrirPerfil(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> startActivity(new Intent(VagasActivity.this, PerfilActivity.class)));
        }
    }

    private void pintarMenu() {
        BottomNavHelper.pintarItem(this, R.id.imgInicioFixoVagas, R.id.txtInicioFixoVagas, true);
        BottomNavHelper.pintarItem(this, R.id.imgFavoritosFixoVagas, R.id.txtFavoritosFixoVagas, false);
        BottomNavHelper.pintarItem(this, R.id.imgPerfilFixoVagas, R.id.txtPerfilFixoVagas, false);
    }

    private static class CardVaga {
        final View card;
        final TextView sigla;
        final TextView empresa;
        final TextView titulo;
        final TextView cidade;
        final TextView tipo;
        final Button detalhes;
        final CheckBox favorito;

        CardVaga(View card, TextView sigla, TextView empresa, TextView titulo, TextView cidade,
                 TextView tipo, Button detalhes, CheckBox favorito) {
            this.card = card;
            this.sigla = sigla;
            this.empresa = empresa;
            this.titulo = titulo;
            this.cidade = cidade;
            this.tipo = tipo;
            this.detalhes = detalhes;
            this.favorito = favorito;
        }
    }
}
