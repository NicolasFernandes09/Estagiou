package br.ulbra.estagiou.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.ulbra.estagiou.R;
import br.ulbra.estagiou.model.Vagas;

// Adapter responsável por ligar os dados da lista de vagas ao RecyclerView.
public class VagasAdapter extends RecyclerView.Adapter<VagasAdapter.ViewHolder> {

    // Lista que armazenará todas as vagas recebidas da API.
    private List<Vagas> listaVagas;

    // Construtor que recebe a lista de vagas.
    public VagasAdapter(List<Vagas> listaVagas) {
        this.listaVagas = listaVagas;
    }

    // Este metodo é chamado sempre que o RecyclerView precisa criar um novo item da lista.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaga, parent, false);

        return new ViewHolder(view);
    }

    // Este metodo recebe uma posição da lista coloca os dados nos componentes da tela.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Obtém a vaga correspondente à posição.
        Vagas vaga = listaVagas.get(position);

        // Preenche os TextViews com os dados da vaga.
        holder.txtTitulo.setText(vaga.getTitulo());
        holder.txtDescricao.setText(vaga.getDescricao());
        holder.txtSalario.setText("Salário: R$ " + vaga.getSalario());
        holder.txtfechamento_vaga.setText("Data de Fechamento da vaga : "+ vaga.getFechamento_vaga());
        holder.txttipo_vaga.setText("Tipo da Vaga : " + vaga.getTipo_vaga());
        holder.txtempresa.setText("Empresa :" + vaga.getEmpresa());
        holder.txttelefone.setText("Telefone" +vaga.getTelefone());

    }

    // Informa ao RecyclerView quantos itens existem na lista.
    @Override
    public int getItemCount() {
        return listaVagas.size();
    }

    // Classe responsável por guardar as referências dos componentes de cada item da lista.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Componentes do layout item_vaga.xml.
        TextView txtTitulo;
        TextView txtDescricao;
        TextView txtSalario;
        TextView txtfechamento_vaga;
        TextView txttipo_vaga , txtempresa ,txttelefone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Liga cada variável ao componente correspondente do XML.
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtSalario = itemView.findViewById(R.id.txtSalario);
            txtfechamento_vaga = itemView.findViewById(R.id.txtfechamento_vaga);
            txttipo_vaga = itemView.findViewById(R.id.txttipo_vaga);
            txtempresa = itemView.findViewById(R.id.txtempresa);
            txttelefone = itemView.findViewById(R.id.txttelefone);
        }
    }
}