package com.raftls.running.authentification.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.raftls.running.app.RunningApp;
import com.raftls.running.app.models.ResponseError;
import com.raftls.running.authentification.events.AuthenticationEvent;
import com.raftls.running.authentification.models.User;
import com.raftls.running.authentification.models.UserLogin;
import com.raftls.running.authentification.models.UserRegister;
import com.raftls.running.network.ApiClient;
import com.raftls.running.storage.services.StorageService;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    public static final String AUTH_PREFERENCES = "auth_preferences";
    public static final String TOKEN_PREFERENCE = "token";
    private static final String COOKIE_HEADER = "Set-Cookie";
    private static UserService instance = null;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private UserService() {
    }

    public void me(Context context, String token) {
        ApiClient.initOkHttp(token);
        ApiClient.getApi().me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new AuthenticationEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new AuthenticationEvent(null));
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                EventBus.getDefault().post(new AuthenticationEvent(null));
            }
        });
    }

    public void login(Context context, String email, String password) {
        UserLogin userLogin = new UserLogin(email, password);
        ApiClient.getApi().login(userLogin).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveUserPreference(context, response.headers().get(COOKIE_HEADER));
                    RunningApp.getInstance().setCurrentUser(response.body());
                    EventBus.getDefault().post(new AuthenticationEvent(response.body()));
                } else {
                    ResponseError responseError = new Gson().fromJson(response.errorBody().charStream(), ResponseError.class);
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Log.d("Running", t.toString());
            }
        });
    }

    public void register(Context context, String firstName, String lastName, String email, String password) {
        UserRegister userRegister = new UserRegister(firstName, lastName, email, password);
        ApiClient.getApi().register(userRegister).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveUserPreference(context, response.headers().get(COOKIE_HEADER));
                    RunningApp.getInstance().setCurrentUser(response.body());
                    EventBus.getDefault().post(new AuthenticationEvent(response.body()));
                } else {
                    ResponseError responseError = new Gson().fromJson(response.errorBody().charStream(), ResponseError.class);
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {

            }
        });
    }

    private void saveUserPreference(Context context, String token) {
        StorageService.getInstance().savePreference(context, AUTH_PREFERENCES, TOKEN_PREFERENCE, token);
        ApiClient.initOkHttp(token);
    }
}