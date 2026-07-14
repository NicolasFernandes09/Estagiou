package br.ulbra.estagiou.api;

import android.content.Context;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import br.ulbra.estagiou.BuildConfig;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.SessaoManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(AndroidJUnit4.class)
public class ApiIntegracaoInstrumentedTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @After
    public void limparSessao() {
        SessaoManager.limpar(context);
    }

    @Test
    public void cadastroLoginEVagasFuncionamComApiLocal() throws Exception {
        assumeTrue(BuildConfig.API_BASE_URL.startsWith("http://127.0.0.1:8765/"));

        AtomicReference<String> erro = new AtomicReference<>();
        CountDownLatch cadastro = new CountDownLatch(1);
        new UsuarioApiClient().registrar(
                context,
                "Filipe Machado",
                "filipe",
                "filipe@email.com",
                "senha123",
                "Desenvolvedor front-end",
                "Perfil pessoal",
                "",
                callback(cadastro, erro)
        );
        assertTrue(cadastro.await(10, TimeUnit.SECONDS));
        assertNull(erro.get());

        CountDownLatch login = new CountDownLatch(1);
        new UsuarioApiClient().login(
                context,
                "filipe",
                "filipe@email.com",
                "senha123",
                callback(login, erro)
        );
        assertTrue(login.await(10, TimeUnit.SECONDS));
        assertNull(erro.get());
        assertTrue(SessaoManager.estaLogado(context));

        CountDownLatch consulta = new CountDownLatch(1);
        AtomicReference<List<VagaDados>> vagas = new AtomicReference<>();
        new VagasApiClient().buscarVagas(context, new VagasApiClient.Callback() {
            @Override
            public void onSuccess(List<VagaDados> resultado) {
                vagas.set(resultado);
                consulta.countDown();
            }

            @Override
            public void onError(String mensagem) {
                erro.set(mensagem);
                consulta.countDown();
            }
        });

        assertTrue(consulta.await(10, TimeUnit.SECONDS));
        assertNull(erro.get());
        assertEquals(2, vagas.get().size());
        assertEquals("Salário: R$ 1.250,50", vagas.get().get(0).salario);
        assertEquals("Telefone: (51) 3000-1001", vagas.get().get(0).telefone);
    }

    private UsuarioApiClient.Callback callback(CountDownLatch latch, AtomicReference<String> erro) {
        return new UsuarioApiClient.Callback() {
            @Override
            public void onSuccess(String mensagem) {
                latch.countDown();
            }

            @Override
            public void onError(String mensagem) {
                erro.set(mensagem);
                latch.countDown();
            }
        };
    }
}
