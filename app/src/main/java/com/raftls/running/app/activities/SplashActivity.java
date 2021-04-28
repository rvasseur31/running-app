package com.raftls.running.app.activities;

import android.os.Bundle;

import com.raftls.running.R;
import com.raftls.running.authentification.activities.BaseAuthenticationActivity;
import com.raftls.running.authentification.events.AuthenticationEvent;
import com.raftls.running.authentification.services.UserService;
import com.raftls.running.storage.services.StorageService;

import org.greenrobot.eventbus.EventBus;

public class SplashActivity extends BaseAuthenticationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onStart() {
        super.onStart();
        isAuthenticated();
    }

    private void isAuthenticated() {
        String token = StorageService.getInstance().getStringPreference(
                getApplicationContext(), UserService.AUTH_PREFERENCES, UserService.TOKEN_PREFERENCE);
        if (token != null) {
            UserService.getInstance().me(getApplicationContext(), token);
        } else {
            EventBus.getDefault().post(new AuthenticationEvent(null));
        }
    }
}