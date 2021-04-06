package com.raftls.running.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.raftls.running.R;
import com.raftls.running.authentification.activities.BaseAuthenticationActivity;
import com.raftls.running.authentification.events.AuthenticationEvent;
import com.raftls.running.authentification.services.UserService;
import com.raftls.running.storage.services.StorageService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
        try {
            SharedPreferences preferences = StorageService.getInstance().getEncryptedPreferences(
                    getApplicationContext(), UserService.AUTH_PREFERENCES);
            String token = preferences.getString(UserService.TOKEN_PREFERENCE, null);
            if (token != null) {
                UserService.getInstance().me(token);
            } else {
                EventBus.getDefault().post(new AuthenticationEvent(null));
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new AuthenticationEvent(null));
        }
    }
}