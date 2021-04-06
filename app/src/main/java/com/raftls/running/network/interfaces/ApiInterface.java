package com.raftls.running.network.interfaces;

import com.raftls.running.authentification.models.User;
import com.raftls.running.authentification.models.UserLogin;
import com.raftls.running.authentification.models.UserMe;
import com.raftls.running.authentification.models.UserRegister;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("auth/login")
    Call<User> login(@Body UserLogin body);

    @POST("auth/register")
    Call<User> register(@Body UserRegister body);

    @POST("auth/me")
    Call<User> me(@Body UserMe body);
}
