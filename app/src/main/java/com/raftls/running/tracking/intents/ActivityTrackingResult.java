package com.raftls.running.tracking.intents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.raftls.running.tracking.ui.TrackingActivity;

public class ActivityTrackingResult extends ActivityResultContract<Void, Boolean> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void ignored) {
        return new Intent(context, TrackingActivity.class);
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent result) {
        return true;
    }

}