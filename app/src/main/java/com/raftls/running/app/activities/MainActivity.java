package com.raftls.running.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.raftls.running.databinding.ActivityMainBinding;
import com.raftls.running.tracking.services.TrackingService;

import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        for (int index = 0; index < 10; index++) {
            TrackingService.getInstance().addPosition(1 + 0.1 * index, 4 + 0.1 * index, 10 * index);
        }

        TrackingService.getInstance().sendRun();
    }
}