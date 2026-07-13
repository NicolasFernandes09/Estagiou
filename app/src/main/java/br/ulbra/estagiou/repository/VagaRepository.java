package br.ulbra.estagiou.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ulbra.estagiou.api.VagasApiClient;
import br.ulbra.estagiou.api.RetrofitClient;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.model.Vagas;
import okhttp3.ResponseBody;

public class VagaRepository {
    public interface Callback {
        void onResult(List<VagaDados> vagas);

        void onError(String mensagem);
    }

    private static final List<VagaDados> VAGAS_PADRAO = Arrays.asList(
            new VagaDados(
                    "suporte_ti",
                    "TS",
                    "TechSul Sistemas",
                    "Estágio em Suporte de TI",
                    "Canoas/RS",
                    "Estágio",
                    "Auxiliar no atendimento técnico, organização de equipamentos e suporte aos usuários.",
                    "Salário: R$ 900 + vale-transporte",
                    "Contato: vagas@techsul.com.br",
                    "Telefone: (51) 3000-1001",
                    "Inscrições até 25/07/2026",
                    "Envie seu currículo em PDF para o e-mail com o assunto: Estágio TI - Seu nome"
            ),
            new VagaDados(
                    "jovem_aprendiz_adm",
                    "FP",
                    "Farmácias do Povo",
                    "Jovem Aprendiz Adm.",
                    "Sapucaia do Sul/RS",
                    "Jovem Aprendiz",
                    "Apoiar rotinas administrativas, organização de documentos e atendimento interno.",
                    "Salário: R$ 900 + vale-transporte",
                    "Contato: rh@farmaciasdopovo.com.br",
                    "Telefone: (51) 3000-1002",
                    "Inscrições até 30/07/2026",
                    "Envie seu currículo com o assunto: Jovem Aprendiz Adm. - Seu nome"
            ),
            new VagaDados(
                    "marketing",
                    "AS",
                    "Atlas Soluções",
                    "Estágio em Marketing",
                    "Esteio/RS",
                    "Estágio",
                    "Apoiar criação de conteúdos, posts para redes sociais e acompanhamento de campanhas.",
                    "Salário: R$ 850 + vale-transporte",
                    "Contato: marketing@atlassolucoes.com.br",
                    "Telefone: (51) 3000-1003",
                    "Inscrições até 28/07/2026",
                    "Envie seu portfólio ou currículo com o assunto: Estágio Marketing - Seu nome"
            ),
            new VagaDados(
                    "designer_freelancer",
                    "CS",
                    "Criativa Studio",
                    "Freelancer em Design",
                    "Canoas/RS",
                    "Freelancer",
                    "Criar peças digitais para redes sociais e materiais simples de divulgação.",
                    "Pagamento por projeto combinado com a empresa",
                    "Contato: jobs@criativastudio.com.br",
                    "Telefone: (51) 3000-1004",
                    "Inscrições até 05/08/2026",
                    "Envie seu portfólio com o assunto: Freelancer Design - Seu nome"
            ),
            new VagaDados(
                    "assistente_clt",
                    "MC",
                    "Mercado Central",
                    "Assistente Administrativo",
                    "Porto Alegre/RS",
                    "CLT",
                    "Atuar em rotinas de escritório, controle de planilhas e apoio ao setor financeiro.",
                    "Salário: R$ 1.850 + benefícios",
                    "Contato: selecao@mercadocentral.com.br",
                    "Telefone: (51) 3000-1005",
                    "Inscrições até 10/08/2026",
                    "Envie seu currículo com o assunto: Assistente Administrativo - Seu nome"
            )
    );

    private static List<VagaDados> vagas = new ArrayList<>(VAGAS_PADRAO);
    private static boolean apiCarregada = false;

    public void carregar(Context context, VagaRepository.Callback callback) {
        if (apiCarregada) {
            callback.onResult(listar());
            return;
        }

        recarregar(context, callback);
    }

    public void recarregar(Context context, VagaRepository.Callback callback) {

        new VagasApiClient().buscarVagas(context, new VagasApiClient.Callback() {
            @Override
            public void onSuccess(List<VagaDados> vagasApi) {
                if (vagasApi != null) {
                    vagas = new ArrayList<>(vagasApi);
                    apiCarregada = true;
                    callback.onResult(listar());
                } else {
                    callback.onError("A API não retornou vagas");
                }
            }

            @Override
            public void onError(String mensagem) {
                apiCarregada = false;
                callback.onError(mensagem);
            }
        });
    }

    public List<VagaDados> listar() {
        return new ArrayList<>(vagas);
    }

    public VagaDados buscarPorId(String id) {
        VagaDados vaga = encontrarPorId(id);
        if (vaga != null) {
            return vaga;
        }
        return vagas.isEmpty() ? VAGAS_PADRAO.get(0) : vagas.get(0);
    }

    public VagaDados encontrarPorId(String id) {
        if (id == null) {
            return null;
        }
        for (VagaDados vaga : vagas) {
            if (vaga.id.equals(id)) {
                return vaga;
            }
        }
        return null;
    }

    public boolean apiCarregada() {
        return apiCarregada;
    }

    public void inserirVaga(Vagas vaga, retrofit2.Callback<ResponseBody> callback) {
        RetrofitClient.getApiService().inserirVaga(vaga).enqueue(callback);
    }

    public void atualizarVaga(Vagas vaga, retrofit2.Callback<ResponseBody> callback) {
        RetrofitClient.getApiService().atualizarVaga(vaga.getVagasId(), vaga).enqueue(callback);
    }

    public void excluirVaga(int id, retrofit2.Callback<ResponseBody> callback) {
        RetrofitClient.getApiService().excluirVaga(id).enqueue(callback);
    }
}
