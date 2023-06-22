package com.sdt.trproject.ksh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sdt.trproject.BuildConfig;
import com.sdt.trproject.ksh.BoardService;

import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String BASE_URL = BuildConfig.SERVER_ADDR;


    public static BoardService getApiService(){return getInstance().create(BoardService.class);}

    private static retrofit2.Retrofit getInstance(){
        Gson gson = new GsonBuilder().setLenient().create();
        return new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
