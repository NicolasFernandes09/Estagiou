package br.ulbra.estagiou.api;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import br.ulbra.estagiou.model.VagaDados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class VagasApiClientInstrumentedTest {
    @Test
    public void converteCamposDoBancoParaVaga() throws Exception {
        String json = "[{"
                + "\"id_vaga\":12,"
                + "\"empresa\":\"TechSul Sistemas\","
                + "\"titulo\":\"Estágio em Suporte de TI\","
                + "\"cidade\":\"Canoas/RS\","
                + "\"tipo_vaga\":\"Estágio\","
                + "\"descricao\":\"Atendimento técnico\","
                + "\"salario\":\"1250.50\","
                + "\"contato\":\"vagas@techsul.com.br\","
                + "\"telefone\":\"(51) 3000-1001\","
                + "\"fechamento_vaga\":\"2026-07-25\","
                + "\"logo\":\"img/techsul.png\""
                + "}]";

        List<VagaDados> vagas = new VagasApiClient().converterResposta(json);

        assertEquals(1, vagas.size());
        VagaDados vaga = vagas.get(0);
        assertEquals("12", vaga.id);
        assertEquals("TechSul Sistemas", vaga.empresa);
        assertEquals("Estágio em Suporte de TI", vaga.titulo);
        assertEquals("Salário: R$ 1.250,50", vaga.salario);
        assertEquals("Contato: vagas@techsul.com.br", vaga.contato);
        assertEquals("Telefone: (51) 3000-1001", vaga.telefone);
        assertEquals("Inscrições até 2026-07-25", vaga.dataLimite);
        assertEquals(ApiConfig.resolverUrlArquivo("img/techsul.png"), vaga.fotoEmpresa);
    }

    @Test
    public void aceitaArrayDentroDoCampoVagas() throws Exception {
        String json = "{\"success\":true,\"vagas\":[{"
                + "\"id\":\"vaga-1\",\"nome_empresa\":\"Empresa X\","
                + "\"cargo\":\"Designer\",\"local\":\"Esteio/RS\","
                + "\"tipo\":\"Freelancer\"}]}";

        List<VagaDados> vagas = new VagasApiClient().converterResposta(json);

        assertEquals(1, vagas.size());
        assertEquals("vaga-1", vagas.get(0).id);
        assertEquals("Empresa X", vagas.get(0).empresa);
        assertEquals("Freelancer", vagas.get(0).tipo);
        assertTrue(vagas.get(0).salario.contains("não informado"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaRespostaDeErroComoVaga() throws Exception {
        new VagasApiClient().converterResposta(
                "{\"success\":false,\"mensagem\":\"Banco indisponível\"}"
        );
    }
}
