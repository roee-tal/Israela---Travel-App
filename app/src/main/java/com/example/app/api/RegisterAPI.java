package com.example.app.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterAPI {
    private static final String BASE_URL = "http://10.9.7.207:3000";
    private static RegisterAPI mInstance;
    private Retrofit retrofit;

    private RegisterAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RegisterAPI getInstance(){
        if(mInstance == null){
            mInstance = new RegisterAPI();
        }
        return mInstance;
    }

    public API getAPI() {
        return retrofit.create(API.class);
    }
}
