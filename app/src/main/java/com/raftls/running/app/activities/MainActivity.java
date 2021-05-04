package com.raftls.running.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.raftls.running.R;
import com.raftls.running.databinding.ActivityMainBinding;
import com.raftls.running.history.ui.HistoryFragment;
import com.raftls.running.tracking.ui.TrackingActivity;

public class MainActivity extends AppCompatActivity  {

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
                Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}