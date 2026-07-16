package br.ulbra.estagiou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.adapter.VagasAdapter;
import br.ulbra.estagiou.controller.VagaController;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.FavoritosStore;
import br.ulbra.estagiou.util.AssistenteHelper;
import br.ulbra.estagiou.util.BottomNavHelper;
import br.ulbra.estagiou.util.TelaHelper;

public class FavoritosActivity extends AppCompatActivity {
    private final List<View> cardsExtras = new ArrayList<>();

    private LinearLayout favoritosContent;
    private View boxMensagemFavoritos;
    private View cardSemFavoritos;
    private VagasAdapter.ViewHolder cardFixo;
    private VagaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        TelaHelper.preencherPainel(this, R.id.cardFavoritos, 36);

        controller = new VagaController();
        inicializarViews();
        configurarAcoes();
        pintarMenu();
        AssistenteHelper.mostrarSePreciso(this, "favoritos",
                "Favoritos",
                "As vagas salvas aparecem aqui. Para remover uma vaga, toque novamente no coração.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarFavoritos();
    }

    private void inicializarViews() {
        favoritosContent = findViewById(R.id.favoritosContent);
        boxMensagemFavoritos = findViewById(R.id.boxMensagemFavoritos);
        cardSemFavoritos = findViewById(R.id.cardSemFavoritos);

        cardFixo = new VagasAdapter.ViewHolder(
                findViewById(R.id.cardFavoritoVaga1),
                findViewById(R.id.txtSiglaFavorito1),
                findViewById(R.id.txtEmpresaFavorito1),
                findViewById(R.id.txtTituloFavorito1),
                findViewById(R.id.txtCidadeFavorito1),
                findViewById(R.id.txtSalarioFavorito1),
                findViewById(R.id.txtTelefoneFavorito1),
                findViewById(R.id.txtTipoFavorito1),
                findViewById(R.id.btnDetalhesFavorito1),
                findViewById(R.id.checkFavoritoSalvo1),
                findViewById(R.id.imgFotoEmpresaFavorito1));
    }

    private void configurarAcoes() {
        abrirVagas(R.id.navInicioFixoFavoritos);
        abrirVagas(R.id.imgInicioFixoFavoritos);
        abrirVagas(R.id.txtInicioFixoFavoritos);

        abrirPerfil(R.id.navPerfilFixoFavoritos);
        abrirPerfil(R.id.imgPerfilFixoFavoritos);
        abrirPerfil(R.id.txtPerfilFixoFavoritos);
    }

    private void carregarFavoritos() {
        controller.carregarVagas(this, new VagaController.CarregamentoCallback() {
            @Override
            public void onResult(List<VagaDados> vagas) {
                mostrarFavoritos();
            }

            @Override
            public void onError(String mensagem) {
                Toast.makeText(FavoritosActivity.this, mensagem + ". Mostrando favoritos salvos no app.", Toast.LENGTH_LONG).show();
                mostrarFavoritos();
            }
        });
    }

    private void mostrarFavoritos() {
        if (favoritosContent == null || cardFixo == null) {
            return;
        }

        limparCardsExtras();

        Set<String> idsFavoritos = FavoritosStore.getIds(this);
        List<VagaDados> favoritas = new ArrayList<>();
        for (VagaDados vaga : controller.listarVagas()) {
            if (idsFavoritos.contains(vaga.id)) {
                favoritas.add(vaga);
            }
        }

        boolean temFavoritos = !favoritas.isEmpty();
        boxMensagemFavoritos.setVisibility(temFavoritos ? View.VISIBLE : View.GONE);
        cardSemFavoritos.setVisibility(temFavoritos ? View.GONE : View.VISIBLE);

        if (!temFavoritos) {
            cardFixo.card.setVisibility(View.GONE);
            return;
        }

        preencherCard(cardFixo, favoritas.get(0));

        for (int i = 1; i < favoritas.size(); i++) {
            View cardExtra = getLayoutInflater().inflate(R.layout.item_favorito_vaga, favoritosContent, false);
            preencherCard(VagasAdapter.criarItemFavorito(cardExtra), favoritas.get(i));

            favoritosContent.addView(cardExtra, favoritosContent.indexOfChild(cardSemFavoritos));
            cardsExtras.add(cardExtra);
        }
    }

    private void preencherCard(VagasAdapter.ViewHolder card, VagaDados vaga) {
        VagasAdapter.preencher(card, vaga, true, new VagasAdapter.Listener() {
            @Override
            public void onDetalhes(VagaDados vagaSelecionada) {
                abrirDetalhes(vagaSelecionada.id);
            }

            @Override
            public void onFavoritoAlterado(VagaDados vagaSelecionada, boolean favorita) {
                if (!favorita) {
                    FavoritosStore.setFavorita(FavoritosActivity.this, vagaSelecionada.id, false);
                    mostrarFavoritos();
                }
            }
        });
    }

    private void limparCardsExtras() {
        for (View cardExtra : cardsExtras) {
            favoritosContent.removeView(cardExtra);
        }
        cardsExtras.clear();
    }

    private void abrirDetalhes(String vagaId) {
        Intent intent = new Intent(FavoritosActivity.this, DetalhesVagaActivity.class);
        intent.putExtra(VagaDados.EXTRA_VAGA_ID, vagaId);
        startActivity(intent);
    }

    private void abrirVagas(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(FavoritosActivity.this, VagasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }
    }

    private void abrirPerfil(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> startActivity(new Intent(FavoritosActivity.this, PerfilActivity.class)));
        }
    }

    private void pintarMenu() {
        BottomNavHelper.pintarItem(this, R.id.imgInicioFixoFavoritos, R.id.txtInicioFixoFavoritos, false);
        BottomNavHelper.pintarItem(this, R.id.imgFavoritosFixoAtivo, R.id.txtFavoritosFixoAtivo, true);
        BottomNavHelper.pintarItem(this, R.id.imgPerfilFixoFavoritos, R.id.txtPerfilFixoFavoritos, false);
    }

}
