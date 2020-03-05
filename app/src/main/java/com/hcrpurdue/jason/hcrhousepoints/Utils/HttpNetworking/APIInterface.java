package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseMessage;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface APIInterface {

    @GET("user/auth-rank")
    Call<AuthRank> getAuthRank(@Header("Authorization") String firebaseToken);

    @POST("link/update")
    Call<ResponseMessage> updateLink(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("link/create")
    Call<ResponseMessage> createLink(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

}
