package com.raftls.running.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.raftls.running.R;
import com.raftls.running.databinding.ActivityMainBinding;
import com.raftls.running.history.events.HistoryRefresh;
import com.raftls.running.history.ui.HistoryFragment;
import com.raftls.running.tracking.intents.ActivityTrackingResult;
import com.raftls.running.tracking.ui.TrackingActivity;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final HistoryFragment historyFragment = new HistoryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        openFragment(historyFragment);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (R.id.home_history == item.getItemId()) {
                openFragment(historyFragment);
                return true;
            } else if (R.id.home_record == item.getItemId()) {
                trackingActivity.launch(null);
                return true;
            }
            return false;
        });
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    ActivityResultLauncher<Void> trackingActivity = registerForActivityResult(new ActivityTrackingResult(),
            result -> {
                if (result) {
                    binding.bottomNavigation.setSelectedItemId(R.id.home_history);
                    EventBus.getDefault().post(new HistoryRefresh());
                }
            });
}