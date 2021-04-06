package com.raftls.running;

import android.app.Application;

import com.raftls.running.authentification.models.User;

public class RunningApp extends Application {

    private User currentUser;
    private static RunningApp instance;

    public static RunningApp getInstance() {
        if (instance == null) {
            instance = new RunningApp();
        }
        return instance;
    }

    private RunningApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
