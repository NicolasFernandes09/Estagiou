package br.ulbra.estagiou.activity;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import br.ulbra.estagiou.R;
import br.ulbra.estagiou.model.VagaDados;
import br.ulbra.estagiou.repository.SessaoManager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class TelasInstrumentedTest {
    private Context context;

    @Before
    public void preparar() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessaoManager.limpar(context);
    }

    @Test
    public void loginAbreComCamposEssenciais() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.edtUsuario)).check(matches(isDisplayed()));
            onView(withId(R.id.edtEmail)).check(matches(isDisplayed()));
            onView(withId(R.id.btnEntrar)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void cadastroAbreComCamposEssenciais() {
        try (ActivityScenario<RegistrarActivity> ignored = ActivityScenario.launch(RegistrarActivity.class)) {
            onView(withId(R.id.edtNome)).check(matches(isDisplayed()));
            onView(withId(R.id.edtUsuarioCadastro)).check(matches(isDisplayed()));
            onView(withId(R.id.btnRegistrar)).perform(scrollTo()).check(matches(isDisplayed()));
        }
    }

    @Test
    public void vagasFavoritosEPerfilAbrem() {
        try (ActivityScenario<VagasActivity> ignored = ActivityScenario.launch(VagasActivity.class)) {
            onView(withId(R.id.edtBuscaVaga)).check(matches(isDisplayed()));
            onView(withId(R.id.btnFiltroTodas)).check(matches(isDisplayed()));
        }

        try (ActivityScenario<FavoritosActivity> ignored = ActivityScenario.launch(FavoritosActivity.class)) {
            onView(withId(R.id.favoritosContent)).check(matches(isDisplayed()));
        }

        try (ActivityScenario<PerfilActivity> ignored = ActivityScenario.launch(PerfilActivity.class)) {
            onView(withId(R.id.btnEditarPerfil)).check(matches(isDisplayed()));
            onView(withId(R.id.btnSairPerfil)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void detalhesAbreComInformacoesDaVaga() {
        Intent intent = new Intent(context, DetalhesVagaActivity.class);
        intent.putExtra(VagaDados.EXTRA_VAGA_ID, "12");

        try (ActivityScenario<DetalhesVagaActivity> ignored = ActivityScenario.launch(intent)) {
            onView(withId(R.id.txtCargoDetalhes)).check(matches(isDisplayed()));
            onView(withId(R.id.txtSalarioVaga)).check(matches(isDisplayed()));
            onView(withId(R.id.txtTelefoneContato)).check(matches(isDisplayed()));
            onView(withId(R.id.btnEnviarCurriculo)).perform(scrollTo()).check(matches(isDisplayed()));
        }
    }
}
