package br.ulbra.estagiou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.FavoritosStore;

public class VagasAdapter extends ListAdapter<VagaDados, VagasAdapter.ViewHolder> {
    public interface Listener {
        void onDetalhes(VagaDados vaga);

        void onFavoritoAlterado(VagaDados vaga, boolean favorita);
    }

    private static final DiffUtil.ItemCallback<VagaDados> COMPARADOR = new DiffUtil.ItemCallback<VagaDados>() {
        @Override
        public boolean areItemsTheSame(@NonNull VagaDados antigo, @NonNull VagaDados novo) {
            return antigo.id.equals(novo.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull VagaDados antigo, @NonNull VagaDados novo) {
            return Objects.equals(antigo.empresa, novo.empresa)
                    && Objects.equals(antigo.titulo, novo.titulo)
                    && Objects.equals(antigo.cidade, novo.cidade)
                    && Objects.equals(antigo.salario, novo.salario)
                    && Objects.equals(antigo.telefone, novo.telefone)
                    && Objects.equals(antigo.tipo, novo.tipo);
        }
    };

    private final Context context;
    private final Listener listener;

    public VagasAdapter(Context context, Listener listener) {
        super(COMPARADOR);
        this.context = context;
        this.listener = listener;
        setHasStableIds(true);
    }

    public void atualizar(List<VagaDados> vagas) {
        submitList(new ArrayList<>(vagas));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaga, parent, false);
        return criarItemVaga(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VagaDados vaga = getItem(position);
        preencher(holder, vaga, FavoritosStore.isFavorita(context, vaga.id), listener);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id.hashCode();
    }

    public static void preencher(ViewHolder holder, VagaDados vaga, boolean favorita, Listener listener) {
        holder.card.setVisibility(View.VISIBLE);
        holder.sigla.setText(vaga.sigla);
        holder.empresa.setText(vaga.empresa);
        holder.titulo.setText(vaga.titulo);
        holder.cidade.setText(vaga.cidade);
        holder.salario.setText(vaga.salario);
        holder.telefone.setText(vaga.telefone);
        holder.tipo.setText(vaga.tipo);
        holder.detalhes.setOnClickListener(v -> listener.onDetalhes(vaga));

        holder.favorito.setOnCheckedChangeListener(null);
        holder.favorito.setChecked(favorita);
        holder.favorito.setOnCheckedChangeListener((buttonView, isChecked) ->
                listener.onFavoritoAlterado(vaga, isChecked));
    }

    public static ViewHolder criarItemVaga(View item) {
        return new ViewHolder(
                item,
                item.findViewById(R.id.txtSiglaEmpresaItem),
                item.findViewById(R.id.txtEmpresaVagaItem),
                item.findViewById(R.id.txtTituloVagaItem),
                item.findViewById(R.id.txtCidadeVagaItem),
                item.findViewById(R.id.txtSalarioVagaItem),
                item.findViewById(R.id.txtTelefoneVagaItem),
                item.findViewById(R.id.txtTipoVagaItem),
                item.findViewById(R.id.btnDetalhesVagaItem),
                item.findViewById(R.id.checkFavoritoVagaItem));
    }

    public static ViewHolder criarItemFavorito(View item) {
        return new ViewHolder(
                item,
                item.findViewById(R.id.txtSiglaFavoritoItem),
                item.findViewById(R.id.txtEmpresaFavoritoItem),
                item.findViewById(R.id.txtTituloFavoritoItem),
                item.findViewById(R.id.txtCidadeFavoritoItem),
                item.findViewById(R.id.txtSalarioFavoritoItem),
                item.findViewById(R.id.txtTelefoneFavoritoItem),
                item.findViewById(R.id.txtTipoFavoritoItem),
                item.findViewById(R.id.btnDetalhesFavoritoItem),
                item.findViewById(R.id.checkFavoritoSalvoItem));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View card;
        public final TextView sigla;
        public final TextView empresa;
        public final TextView titulo;
        public final TextView cidade;
        public final TextView salario;
        public final TextView telefone;
        public final TextView tipo;
        public final Button detalhes;
        public final CheckBox favorito;

        public ViewHolder(View card, TextView sigla, TextView empresa, TextView titulo,
                          TextView cidade, TextView salario, TextView telefone, TextView tipo,
                          Button detalhes, CheckBox favorito) {
            super(card);
            this.card = card;
            this.sigla = sigla;
            this.empresa = empresa;
            this.titulo = titulo;
            this.cidade = cidade;
            this.salario = salario;
            this.telefone = telefone;
            this.tipo = tipo;
            this.detalhes = detalhes;
            this.favorito = favorito;
        }
    }
}
