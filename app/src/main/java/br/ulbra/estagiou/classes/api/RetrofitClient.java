package br.ulbra.estagiou.classes.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new retrofit.Builder().baseUrl( "http://172.29.20.89/Estagiou/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return retrofit;
    }

}