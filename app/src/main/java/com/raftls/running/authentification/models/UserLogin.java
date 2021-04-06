package com.raftls.running.authentification.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLogin {
    @SerializedName("email")
    @Expose
    private final String email;

    @SerializedName("password")
    @Expose
    private final String password;

    public UserLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
