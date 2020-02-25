package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {

    private static APIHelper instance;
    private static Context context;
    private static APIInterface apiInterface;
    public static String domain = "https://us-central1-purdue-hcr-test.cloudfunctions.net/";
    //public static String domain = "https://us-central1-hcr-points.cloudfunctions.net/";

    APIHelper(Context context){
        this.context = context;
        if(apiInterface == null) {
            Gson gson = new GsonBuilder().setLenient().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(domain)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(APIInterface.class);
        }
    }

    public static APIHelper getInstance(Context context) {
        if(instance == null){
            instance = new APIHelper(context);
        }
        return instance;
    }

    private String getFirebaseToken(){
        return "Bearer "+CacheManager.getInstance(context).getUser().getFirebaseToken();
    }

    public Call<AuthRank> getRank(){
        return apiInterface.getAuthRank(getFirebaseToken());
    }

    public Call<String> createLink(String description, int pointTypeId, boolean isSingleUse){
        HashMap<String, Object> body = new HashMap<>();
        body.put("description", description);
        body.put("point_id", pointTypeId);
        body.put("single_use", isSingleUse);
        return apiInterface.createLink(getFirebaseToken(), body);
    }

}
