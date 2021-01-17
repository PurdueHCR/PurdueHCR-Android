package com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking;

import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.Models.EventList;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointTypeList;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface APIInterface {

    @GET("user/auth-rank")
    Call<AuthRank> getAuthRank(@Header("Authorization") String firebaseToken, @Header("Content-Type") String contentType);

    @GET("event/feed")
    Call<EventList> getEventFeed(@Header("Authorization") String firebaseToken);

    @GET("point_type/submittable")
    Call<PointTypeList> getPointTypes(@Header("Authorization") String firebaseToken, @Header("Content-Type") String contentType);

    /* @GET("events/")
     Call<Event> getEventFeed(@Header("Authorization") String firebaseToken);
 */
    @PUT("link/update")
    Call<ResponseMessage> updateLink(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("event")
    Call<ResponseMessage> createEvent(@Header("Authorization")  String firebaseToken,@Header("Content-Type") String contentType, @Body Event event);

    @PUT("event")
    Call<ResponseMessage> updateEvent(@Header("Authorization")  String firebaseToken,@Header("Content-Type") String contentType, @Body Event event);

    @POST("link/create")
    Call<ResponseMessage> createLink(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("user/create")
    Call<User> createUser(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("user/submitPoint")
    Call<ResponseMessage> submitPoint(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("user/submitLink")
    Call<ResponseMessage> submitLink(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("point_log/messages")
    Call<ResponseMessage> postPointLogMessage(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("point_log/handle")
    Call<ResponseMessage> handlePointLog(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

    @POST("point_log/viewMessages")
    Call<ResponseMessage> viewMessages(@Header("Authorization") String firebaseToken, @Body Map<String, Object> body);

}
