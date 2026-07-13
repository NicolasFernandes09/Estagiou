package br.ulbra.estagiou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.EstagiouApplication;
import br.ulbra.estagiou.adapter.VagasAdapter;
import br.ulbra.estagiou.controller.VagaController;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.FavoritosStore;
import br.ulbra.estagiou.util.AssistenteHelper;
import br.ulbra.estagiou.util.BottomNavHelper;
import br.ulbra.estagiou.util.TelaHelper;

public class VagasActivity extends AppCompatActivity {
    private VagasAdapter adapter;
    private VagaController controller;
    private RecyclerView recyclerVagas;
    private View progressVagas;
    private View boxErroVagas;
    private TextView txtErroVagas;
    private TextView txtSemResultadosVagas;
    private EditText edtBuscaVaga;
    private String filtroAtual = "Todas";
    private boolean carregando;
    private boolean rolarParaInicio = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vagas);
        TelaHelper.preencherPainel(this, R.id.vagasCard, 36);

        controller = new VagaController();
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
        if (adapter != null) {
            aplicarFiltroBusca();
            adapter.notifyDataSetChanged();
        }
    }

    private void inicializarViews() {
        recyclerVagas = findViewById(R.id.recyclerVagas);
        progressVagas = findViewById(R.id.progressVagas);
        boxErroVagas = findViewById(R.id.boxErroVagas);
        txtErroVagas = findViewById(R.id.txtErroVagas);
        edtBuscaVaga = findViewById(R.id.edtBuscaVaga);
        txtSemResultadosVagas = findViewById(R.id.txtSemResultadosVagas);

        adapter = new VagasAdapter(this, new VagasAdapter.Listener() {
            @Override
            public void onDetalhes(VagaDados vaga) {
                abrirDetalhes(vaga.id);
            }

            @Override
            public void onFavoritoAlterado(VagaDados vaga, boolean favorita) {
                FavoritosStore.setFavorita(VagasActivity.this, vaga.id, favorita);
            }
        });

        recyclerVagas.setLayoutManager(new LinearLayoutManager(this));
        recyclerVagas.setAdapter(adapter);

        Button tentarNovamente = findViewById(R.id.btnTentarNovamenteVagas);
        tentarNovamente.setOnClickListener(v -> carregarVagas());
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
        edtBuscaVaga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rolarParaInicio = true;
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
        carregando = true;
        atualizarEstadoCarregamento();

        controller.recarregarVagas(this, new VagaController.CarregamentoCallback() {
            @Override
            public void onResult(List<VagaDados> vagas) {
                carregando = false;
                ((EstagiouApplication) getApplication()).definirStatusApi(true);
                boxErroVagas.setVisibility(View.GONE);
                rolarParaInicio = true;
                aplicarFiltroBusca();
            }

            @Override
            public void onError(String mensagem) {
                carregando = false;
                ((EstagiouApplication) getApplication()).definirStatusApi(false);
                txtErroVagas.setText(mensagem + ". Mostrando as vagas salvas no aplicativo.");
                boxErroVagas.setVisibility(View.VISIBLE);
                aplicarFiltroBusca();
            }
        });
    }

    public void recarregarAutomaticamente() {
        carregarVagas();
    }

    private void configurarFiltro(int id, String filtro) {
        Button botao = findViewById(id);
        botao.setOnClickListener(v -> {
            filtroAtual = filtro;
            rolarParaInicio = true;
            atualizarEstadoFiltros();
            aplicarFiltroBusca();
        });
    }

    private void aplicarFiltroBusca() {
        if (adapter == null || controller == null) {
            return;
        }

        String busca = edtBuscaVaga == null ? "" : edtBuscaVaga.getText().toString();
        List<VagaDados> vagasFiltradas = new ArrayList<>();
        for (VagaDados vaga : controller.listarVagas()) {
            if (vaga.combinaComFiltro(filtroAtual) && vaga.combinaComBusca(busca)) {
                vagasFiltradas.add(vaga);
            }
        }

        adapter.atualizar(vagasFiltradas);
        if (rolarParaInicio && !vagasFiltradas.isEmpty()) {
            recyclerVagas.post(() -> recyclerVagas.scrollToPosition(0));
            rolarParaInicio = false;
        }
        boolean semResultados = vagasFiltradas.isEmpty() && !carregando;
        txtSemResultadosVagas.setVisibility(semResultados ? View.VISIBLE : View.GONE);
        recyclerVagas.setVisibility(!carregando && !semResultados ? View.VISIBLE : View.GONE);
        atualizarEstadoCarregamento();
        atualizarEstadoFiltros();
    }

    private void atualizarEstadoCarregamento() {
        progressVagas.setVisibility(carregando ? View.VISIBLE : View.GONE);
        if (carregando) {
            recyclerVagas.setVisibility(View.GONE);
            txtSemResultadosVagas.setVisibility(View.GONE);
            boxErroVagas.setVisibility(View.GONE);
        }
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
        view.setOnClickListener(v -> startActivity(new Intent(VagasActivity.this, FavoritosActivity.class)));
    }

    private void abrirPerfil(int id) {
        View view = findViewById(id);
        view.setOnClickListener(v -> startActivity(new Intent(VagasActivity.this, PerfilActivity.class)));
    }

    private void pintarMenu() {
        BottomNavHelper.pintarItem(this, R.id.imgInicioFixoVagas, R.id.txtInicioFixoVagas, true);
        BottomNavHelper.pintarItem(this, R.id.imgFavoritosFixoVagas, R.id.txtFavoritosFixoVagas, false);
        BottomNavHelper.pintarItem(this, R.id.imgPerfilFixoVagas, R.id.txtPerfilFixoVagas, false);
    }
}
