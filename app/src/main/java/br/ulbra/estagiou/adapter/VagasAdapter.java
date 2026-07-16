package br.ulbra.estagiou.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
                    && Objects.equals(antigo.tipo, novo.tipo)
                    && Objects.equals(antigo.fotoEmpresa, novo.fotoEmpresa);
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
        carregarFoto(holder, vaga);

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
                item.findViewById(R.id.checkFavoritoVagaItem),
                item.findViewById(R.id.imgFotoEmpresaItem));
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
                item.findViewById(R.id.checkFavoritoSalvoItem),
                item.findViewById(R.id.imgFotoEmpresaFavoritoItem));
    }

    private static void carregarFoto(ViewHolder holder, VagaDados vaga) {
        if (holder.fotoEmpresa == null) {
            return;
        }

        holder.fotoEmpresa.setClipToOutline(true);
        Glide.with(holder.fotoEmpresa).clear(holder.fotoEmpresa);
        if (vaga.fotoEmpresa == null || vaga.fotoEmpresa.trim().isEmpty()) {
            holder.fotoEmpresa.setVisibility(View.GONE);
            holder.sigla.setVisibility(View.VISIBLE);
            return;
        }

        holder.fotoEmpresa.setVisibility(View.VISIBLE);
        holder.sigla.setVisibility(View.GONE);
        Glide.with(holder.fotoEmpresa)
                .load(vaga.fotoEmpresa)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        holder.fotoEmpresa.setVisibility(View.GONE);
                        holder.sigla.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        holder.fotoEmpresa.setVisibility(View.VISIBLE);
                        holder.sigla.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.fotoEmpresa);
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
        public final ImageView fotoEmpresa;

        public ViewHolder(View card, TextView sigla, TextView empresa, TextView titulo,
                          TextView cidade, TextView salario, TextView telefone, TextView tipo,
                          Button detalhes, CheckBox favorito) {
            this(card, sigla, empresa, titulo, cidade, salario, telefone, tipo,
                    detalhes, favorito, null);
        }

        public ViewHolder(View card, TextView sigla, TextView empresa, TextView titulo,
                          TextView cidade, TextView salario, TextView telefone, TextView tipo,
                          Button detalhes, CheckBox favorito, ImageView fotoEmpresa) {
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
            this.fotoEmpresa = fotoEmpresa;
        }
    }
}
