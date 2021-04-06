package com.raftls.running.authentification.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMe {
    @SerializedName("token")
    @Expose
    private final String token;

    public UserMe(String token) {
        this.token = token;
    }
}
