package br.ulbra.estagiou.classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.ulbra.estagiou.R;

public class FavoritosActivity extends AppCompatActivity {
    private final List<View> cardsExtras = new ArrayList<>();

    private LinearLayout favoritosContent;
    private View boxMensagemFavoritos;
    private View cardSemFavoritos;
    private CardFavorito cardFixo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        TelaHelper.preencherPainel(this, R.id.cardFavoritos, 36);

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

        cardFixo = new CardFavorito(
                findViewById(R.id.cardFavoritoVaga1),
                findViewById(R.id.txtSiglaFavorito1),
                findViewById(R.id.txtEmpresaFavorito1),
                findViewById(R.id.txtTituloFavorito1),
                findViewById(R.id.txtCidadeFavorito1),
                findViewById(R.id.txtTipoFavorito1),
                findViewById(R.id.btnDetalhesFavorito1),
                findViewById(R.id.checkFavoritoSalvo1));
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
        VagasRepository.carregar(this, new VagasRepository.Callback() {
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
        for (VagaDados vaga : VagasRepository.listar()) {
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
            preencherCard(new CardFavorito(
                    cardExtra,
                    cardExtra.findViewById(R.id.txtSiglaFavoritoItem),
                    cardExtra.findViewById(R.id.txtEmpresaFavoritoItem),
                    cardExtra.findViewById(R.id.txtTituloFavoritoItem),
                    cardExtra.findViewById(R.id.txtCidadeFavoritoItem),
                    cardExtra.findViewById(R.id.txtTipoFavoritoItem),
                    cardExtra.findViewById(R.id.btnDetalhesFavoritoItem),
                    cardExtra.findViewById(R.id.checkFavoritoSalvoItem)), favoritas.get(i));

            favoritosContent.addView(cardExtra, favoritosContent.indexOfChild(cardSemFavoritos));
            cardsExtras.add(cardExtra);
        }
    }

    private void preencherCard(CardFavorito card, VagaDados vaga) {
        card.card.setVisibility(View.VISIBLE);
        card.sigla.setText(vaga.sigla);
        card.empresa.setText(vaga.empresa);
        card.titulo.setText(vaga.titulo);
        card.cidade.setText(vaga.cidade);
        card.tipo.setText(vaga.tipo);
        card.detalhes.setOnClickListener(v -> abrirDetalhes(vaga.id));

        card.favorito.setOnCheckedChangeListener(null);
        card.favorito.setChecked(true);
        card.favorito.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                FavoritosStore.setFavorita(FavoritosActivity.this, vaga.id, false);
                mostrarFavoritos();
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

    private static class CardFavorito {
        final View card;
        final TextView sigla;
        final TextView empresa;
        final TextView titulo;
        final TextView cidade;
        final TextView tipo;
        final Button detalhes;
        final CheckBox favorito;

        CardFavorito(View card, TextView sigla, TextView empresa, TextView titulo, TextView cidade,
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
