package com.raftls.running.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.raftls.running.R;
import com.raftls.running.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}