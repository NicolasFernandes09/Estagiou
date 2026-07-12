package br.ulbra.estagiou.funcoes;

import java.util.List;

import br.ulbra.estagiou.model.Vagas;
import br.ulbra.estagiou.repository.VagaRepository;

import retrofit2.Call;
import retrofit2.Callback;

public class VagaController {

    private VagaRepository repository;

    public VagaController() {

        // Cria uma nova instância do Repository
        // para poder acessar os métodos da API.
        repository = new VagaRepository();

    }

    /* Metodo responsave por carregar as vagas
    Recebe um Callback porque a resposta da API não chega imediatamente.
    Quando a API responder , o callback avisa se deu certo ou se ocorreu algum erro. */
    public void carregarVagas(
            Callback<List<Vagas>> callback) {

    /* Chama o metodo buscarVagas() do Repository.
     O Repository vai chamar a API, buscar o arquivo vagas.php, e retornar a lista de vagas. */
        repository.buscarVagas(callback);

    }

}
