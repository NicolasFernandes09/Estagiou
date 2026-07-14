package br.ulbra.estagiou.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VagaDadosTest {
    private final VagaDados vaga = new VagaDados(
            "1",
            "TS",
            "TechSul Soluções",
            "Estágio em Suporte de TI",
            "Canoas/RS",
            "Estágio",
            "Atendimento técnico",
            "Salário: R$ 900,00",
            "Contato: vagas@techsul.com.br",
            "Telefone: (51) 3000-1001",
            "Inscrições até 25/07/2026",
            "Envie seu currículo"
    );

    @Test
    public void buscaIgnoraAcentosEMaiusculas() {
        assertTrue(vaga.combinaComBusca("estagio"));
        assertTrue(vaga.combinaComBusca("SOLUCOES"));
        assertTrue(vaga.combinaComBusca("canoas"));
        assertFalse(vaga.combinaComBusca("marketing"));
    }

    @Test
    public void filtroRespeitaTipoEPermiteTodas() {
        assertTrue(vaga.combinaComFiltro("Todas"));
        assertTrue(vaga.combinaComFiltro("Estágio"));
        assertTrue(vaga.combinaComFiltro("estágio"));
        assertFalse(vaga.combinaComFiltro("CLT"));
    }
}
