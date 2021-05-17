package com.raftls.running.authentification.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.raftls.running.authentification.events.AuthenticationEvent;
import com.raftls.running.app.activities.MainActivity;
import com.raftls.running.storage.services.StorageService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.raftls.running.authentification.services.UserService.AUTH_PREFERENCES;
import static com.raftls.running.authentification.services.UserService.TOKEN_PREFERENCE;

public class BaseAuthenticationActivity extends AppCompatActivity {

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationEvent(AuthenticationEvent event) {
        Class<?> clazz;
        if(event.isAuthenticated()) {
             clazz = MainActivity.class;
        } else {
            StorageService.getInstance().clearPreference(getApplicationContext(), AUTH_PREFERENCES, TOKEN_PREFERENCE);
            clazz = LoginActivity.class;
        }
        Intent intent = new Intent(getApplicationContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
