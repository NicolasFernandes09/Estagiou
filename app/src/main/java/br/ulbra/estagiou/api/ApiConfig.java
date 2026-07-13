package br.ulbra.estagiou.api;

import br.ulbra.estagiou.BuildConfig;

public class ApiConfig {
    public static final String BASE_URL = BuildConfig.API_BASE_URL;
    public static final String VAGAS_URL = BASE_URL + "vagas.php";
    public static final String USUARIOS_URL = BASE_URL + "usuarios.php";
}
