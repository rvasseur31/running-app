package com.raftls.running.authentification.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.raftls.running.authentification.events.AuthenticationEvent;
import com.raftls.running.app.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
            clazz = LoginActivity.class;
        }
        Intent intent = new Intent(getApplicationContext(), clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
