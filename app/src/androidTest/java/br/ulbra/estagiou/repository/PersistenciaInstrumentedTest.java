package br.ulbra.estagiou.repository;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PersistenciaInstrumentedTest {
    private Context context;

    @Before
    public void preparar() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        limpar();
    }

    @After
    public void finalizar() {
        limpar();
    }

    @Test
    public void sessaoFuncionaMesmoSemToken() {
        SessaoManager.salvar(context, "", 7, "filipe");

        assertTrue(SessaoManager.estaLogado(context));
        assertEquals(7, SessaoManager.usuarioId(context));
        assertEquals("filipe", SessaoManager.usuario(context));

        SessaoManager.limpar(context);
        assertFalse(SessaoManager.estaLogado(context));
    }

    @Test
    public void favoritoPodeSerAdicionadoERemovido() {
        FavoritosStore.setFavorita(context, "vaga-12", true);
        assertTrue(FavoritosStore.isFavorita(context, "vaga-12"));

        FavoritosStore.setFavorita(context, "vaga-12", false);
        assertFalse(FavoritosStore.isFavorita(context, "vaga-12"));
    }

    @Test
    public void perfilMantemDescricoesEFoto() {
        UsuarioStore.salvarUsuario(
                context,
                "Filipe Machado",
                "filipe",
                "filipe@email.com",
                "Descrição profissional",
                "Descrição pessoal",
                "content://foto"
        );

        UsuarioStore.UsuarioDados dados = UsuarioStore.buscarUsuario(context, "filipe");
        assertEquals("Filipe Machado", dados.nome);
        assertEquals("filipe@email.com", dados.email);
        assertEquals("Descrição profissional", dados.descricaoProfissional);
        assertEquals("Descrição pessoal", dados.descricaoPessoal);
        assertEquals("content://foto", dados.foto);
    }

    private void limpar() {
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("usuarios_estagiou", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("favoritos_vagas", Context.MODE_PRIVATE)
                .edit()
                .putStringSet("ids_vagas_favoritas", Collections.emptySet())
                .commit();
    }
}
