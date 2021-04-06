package com.raftls.running.authentification.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserRegister extends UserLogin {

    @SerializedName("firstName")
    @Expose
    private final String firstName;

    @SerializedName("lastName")
    @Expose
    private final String lastName;

    public UserRegister(String firstName, String lastName, String email, String password) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
