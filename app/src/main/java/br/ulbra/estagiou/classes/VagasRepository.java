package br.ulbra.estagiou.classes;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VagasRepository {
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
                    "Bolsa auxílio: R$ 900 + vale-transporte",
                    "Contato: vagas@techsul.com.br",
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
                    "Bolsa aprendizagem + vale-transporte",
                    "Contato: rh@farmaciasdopovo.com.br",
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
                    "Bolsa auxílio: R$ 850 + vale-transporte",
                    "Contato: marketing@atlassolucoes.com.br",
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
                    "Inscrições até 10/08/2026",
                    "Envie seu currículo com o assunto: Assistente Administrativo - Seu nome"
            )
    );

    private static List<VagaDados> vagas = new ArrayList<>(VAGAS_PADRAO);
    private static boolean apiCarregada = false;

    public static void carregar(Context context, Callback callback) {
        if (apiCarregada) {
            callback.onResult(listar());
            return;
        }

        new VagasApiClient().buscarVagas(context, new VagasApiClient.Callback() {
            @Override
            public void onSuccess(List<VagaDados> vagasApi) {
                if (vagasApi != null && !vagasApi.isEmpty()) {
                    vagas = new ArrayList<>(vagasApi);
                    apiCarregada = true;
                    callback.onResult(listar());
                } else {
                    callback.onError("A API não retornou vagas");
                }
            }

            @Override
            public void onError(String mensagem) {
                callback.onError(mensagem);
            }
        });
    }

    public static List<VagaDados> listar() {
        return new ArrayList<>(vagas);
    }

    public static VagaDados buscarPorId(String id) {
        VagaDados vaga = encontrarPorId(id);
        if (vaga != null) {
            return vaga;
        }
        return vagas.isEmpty() ? VAGAS_PADRAO.get(0) : vagas.get(0);
    }

    public static VagaDados encontrarPorId(String id) {
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

    public static boolean apiCarregada() {
        return apiCarregada;
    }
}
