package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseMessage;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {

    private static APIHelper instance;
    private static Context context;
    private static APIInterface apiInterface;
    //public static String domain = "http://10.0.2.2:5001/purdue-hcr-test/us-central1/";
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
        System.out.println("CALLLING TO GET RANK");
        return apiInterface.getAuthRank(getFirebaseToken());
    }

    public Call<ResponseMessage> createLink(String description, int pointTypeId, boolean isSingleUse){
        HashMap<String, Object> body = new HashMap<>();
        body.put("description", description);
        body.put("point_id", pointTypeId);
        body.put("single_use", isSingleUse);
        return apiInterface.createLink(getFirebaseToken(), body);
    }

    public Call<ResponseMessage> updateLink(String linkId, Map<String, Object> data){
        data.put("link_id", linkId);
        return apiInterface.updateLink(getFirebaseToken(), data);
    }

}
