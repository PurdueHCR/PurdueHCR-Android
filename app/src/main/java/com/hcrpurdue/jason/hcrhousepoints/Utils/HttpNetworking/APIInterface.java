package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface APIInterface {

    @GET("rank")
    Call<Integer> getUserRank(@Header("User-Auth") String userId);

}
