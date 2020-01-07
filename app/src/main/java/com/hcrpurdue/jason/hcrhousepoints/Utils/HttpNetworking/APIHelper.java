package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {

    private static APIInterface apiInterface;
    public static String domain = "https://us-central1-purdue-hcr-test.cloudfunctions.net/webApi/api/v1/";

    public static APIInterface getInstance() {
        if(apiInterface == null) {
            Gson gson = new GsonBuilder().setLenient().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(domain)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(APIInterface.class);
        }
        return apiInterface;
    }

}
