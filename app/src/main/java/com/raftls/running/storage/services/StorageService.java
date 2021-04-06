package com.raftls.running.storage.services;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class StorageService {
    private static StorageService instance;

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
        }
        return instance;
    }

    private StorageService() {
    }

    private MasterKey masterKey(Context context) throws GeneralSecurityException, IOException {
        return new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
    }

    public SharedPreferences getEncryptedPreferences(Context context, String filename) throws GeneralSecurityException, IOException {
        return EncryptedSharedPreferences.create(
                context,
                filename,
                masterKey(context),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
}