package com.raftls.running.authentification.models;

import com.google.gson.annotations.SerializedName;

public enum UserRole {
    @SerializedName("admin")
    ADMIN,
    @SerializedName("user")
    USER
}