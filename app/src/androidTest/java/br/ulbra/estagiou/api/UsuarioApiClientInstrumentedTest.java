package br.ulbra.estagiou.api;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UsuarioApiClientInstrumentedTest {
    @Test
    public void loginMantemCamposNaOrdemCorreta() {
        Map<String, String> params = UsuarioApiClient.parametrosLogin(
                "filipe", "filipe@email.com", "senha123"
        );

        assertEquals("login", params.get("action"));
        assertEquals("login", params.get("acao"));
        assertEquals("filipe", params.get("usuario"));
        assertEquals("filipe@email.com", params.get("email"));
        assertEquals("senha123", params.get("senha"));
    }

    @Test
    public void cadastroMantemTodosOsCamposNaOrdemCorreta() {
        Map<String, String> params = UsuarioApiClient.parametrosRegistro(
                "Filipe Machado",
                "filipe",
                "filipe@email.com",
                "senha123",
                "Descrição profissional",
                "Descrição pessoal",
                "content://foto"
        );

        assertEquals("registrar", params.get("action"));
        assertEquals("registrar", params.get("acao"));
        assertEquals("Filipe Machado", params.get("nome"));
        assertEquals("filipe", params.get("usuario"));
        assertEquals("filipe@email.com", params.get("email"));
        assertEquals("senha123", params.get("senha"));
        assertEquals("Descrição profissional", params.get("descricao_profissional"));
        assertEquals("Descrição pessoal", params.get("descricao_pessoal"));
        assertEquals("content://foto", params.get("foto"));
    }

    @Test
    public void interpretaRespostasCompativeisDaApi() {
        assertTrue(UsuarioApiClient.respostaOk("{\"success\":true}"));
        assertFalse(UsuarioApiClient.respostaOk("{\"success\":false}"));
        assertTrue(UsuarioApiClient.respostaLoginOk(
                "{\"usuario\":{\"id_usuario\":7,\"email\":\"filipe@email.com\"}}"
        ));
        assertTrue(UsuarioApiClient.respostaLoginOk(
                "{\"success\":true,\"usuario\":{\"id_usuario\":7}}"
        ));
        assertFalse(UsuarioApiClient.respostaLoginOk("{\"mensagem\":\"Credenciais inválidas\"}"));
    }

    @Test
    public void atualizacaoDePerfilEnviaCamposParaApi() {
        Map<String, String> params = UsuarioApiClient.parametrosAtualizacao(
                7,
                "filipe",
                "Filipe Machado",
                "filipe@email.com",
                "Descrição profissional",
                "Descrição pessoal",
                "content://foto"
        );

        assertEquals("atualizar", params.get("action"));
        assertEquals("PUT", params.get("_method"));
        assertEquals("7", params.get("id_usuario"));
        assertEquals("filipe", params.get("usuario"));
        assertEquals("Descrição profissional", params.get("descricao_profissional"));
        assertEquals("Descrição pessoal", params.get("descricao_pessoal"));
        assertFalse(params.containsKey("foto"));
    }
}
