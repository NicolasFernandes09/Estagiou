package br.ulbra.estagiou.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


// Essa classe cria a conexão com a API.


public class RetrofitClient {

    // Guarda uma única instância do Retrofit.
    // Evita criar várias conexões desnecessárias.
    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        // Verifica se o Retrofit já foi criado.
        if(retrofit == null){
            // Cria o objeto Retrofit
            retrofit = new Retrofit.Builder()
                    // Endereço principal da API.
                    // Sempre termina com "/"
                    .baseUrl("http://10.0.2.2/Estagiou/api/")
                    // Gson transforma JSON em objetos Java.
                    .addConverterFactory(GsonConverterFactory.create())
                    // Finaliza a configuração.
                    .build();
        }

        // Retorna a conexão pronta.

        return retrofit;
    }

}
