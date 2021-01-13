package com.intan.datapenduduk.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    public static String BASE_URL = "http://192.168.100.3/penduduk/";
    private static RetrofitClient mInstanse;
    private Retrofit retrofit;
    private Gson gson;

    private RetrofitClient(){

        gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstanse(){
        if (mInstanse == null){
            mInstanse = new RetrofitClient();
        }
        return mInstanse;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
