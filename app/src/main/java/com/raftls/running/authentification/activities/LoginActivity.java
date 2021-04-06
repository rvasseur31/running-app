package com.raftls.running.authentification.activities;

import android.content.Intent;
import android.os.Bundle;

import com.raftls.running.authentification.services.UserService;
import com.raftls.running.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseAuthenticationActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            UserService.getInstance().login(getApplicationContext(), email, password);
        });

        binding.btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }
}