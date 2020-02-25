package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface APIInterface {

    @GET("user/auth-rank")
    Call<AuthRank> getAuthRank(@Header("Authorization") String firebaseToken);



    @POST("link/create")
    Call<void> createLink(@Header("Authorization") String firebaseToken, @Body HashMap<String, Object> body);

}
