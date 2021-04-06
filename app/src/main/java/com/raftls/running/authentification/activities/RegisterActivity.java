package com.raftls.running.authentification.activities;

import android.os.Bundle;
import android.view.View;

import com.raftls.running.authentification.services.UserService;
import com.raftls.running.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseAuthenticationActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> {

        });

        binding.btnRegister.setOnClickListener(view -> {
            String firstName = binding.etFirstName.getText().toString();
            String lastName = binding.etLastName.getText().toString();
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            UserService.getInstance().register(getApplicationContext(), firstName, lastName, email, password);
        });

        binding.btnLogin.setOnClickListener(view -> onBackPressed());
    }
}