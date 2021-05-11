package com.raftls.running.network.interfaces;

import com.raftls.running.authentification.models.User;
import com.raftls.running.authentification.models.UserLogin;
import com.raftls.running.authentification.models.UserMe;
import com.raftls.running.authentification.models.UserRegister;
import com.raftls.running.tracking.models.geojson.Run;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("auth/login")
    Call<User> login(@Body UserLogin body);

    @POST("auth/register")
    Call<User> register(@Body UserRegister body);

    @GET("auth/me")
    Call<User> me();

    @POST("courses")
    Call<Void> uploadRun(@Body Run run);

    @GET("courses/all")
    Call<ArrayList<Run>> getAllRun();

    @DELETE("courses/{id}")
    Call<Void> deleteRun(@Path("id") String id);

    @POST("courses/multiple")
    Call<Void> deleteMultipleRuns(@Body ArrayList<String> ids);
}
