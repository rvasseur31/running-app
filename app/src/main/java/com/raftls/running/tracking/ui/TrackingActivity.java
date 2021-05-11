package com.raftls.running.tracking.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.raftls.running.R;
import com.raftls.running.databinding.ActivityTrackingBinding;
import com.raftls.running.network.ApiClient;
import com.raftls.running.notification.models.ENotificationType;
import com.raftls.running.notification.services.NotificationService;
import com.raftls.running.tracking.models.ETrackingState;
import com.raftls.running.tracking.services.TrackingService;

import org.jetbrains.annotations.NotNull;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = TrackingActivity.class.getSimpleName();
    private boolean isMapViewSelected = true;
    private ActivityTrackingBinding binding;
    private final TrackingService trackingService = TrackingService.getInstance();
    private final MapFragment mapFragment = new MapFragment();
    private MaterialDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        openFragment(mapFragment);

        binding.startTracking.setOnClickListener(view -> {
            if (trackingService.trackingState == ETrackingState.RUNNING) {
                binding.startTracking.setIcon(ContextCompat.getDrawable(TrackingActivity.this, R.drawable.ic_play_arrow));
                trackingService.pauseTracking();
                binding.stopTracking.setVisibility(View.GONE);
            } else if (trackingService.trackingState == ETrackingState.PAUSED || trackingService.trackingState == ETrackingState.STOPPED) {
                binding.startTracking.setIcon(ContextCompat.getDrawable(TrackingActivity.this, R.drawable.ic_pause));
                binding.mapViewButton.setVisibility(View.VISIBLE);
                binding.stopTracking.setVisibility(View.VISIBLE);
                if (!trackingService.startTracking(this, mapFragment.getMapboxMap())) {
                    Log.e(TAG, "No permission");
                }
            }
        });

        binding.stopTracking.setOnClickListener(view -> {
            exitDialog = new MaterialDialog.Builder(this)
                    .setTitle(getString(R.string.stop_tracking_alert_title))
                    .setMessage(getString(R.string.stop_tracking_alert_description))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.stop_tracking_alert_confirm), R.drawable.ic_delete, (dialogInterface, which) -> {
                        exitDialog.dismiss();
                        exitDialog = new MaterialDialog.Builder(this)
                                .setAnimation(R.raw.loading)
                                .setTitle(getString(R.string.loading))
                                .setCancelable(false)
                                .build();
                        exitDialog.show();
                        ApiClient.getApi().uploadRun(trackingService.getCurrentRun()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                                // Run deleted
                                exitDialog.dismiss();
                                NotificationService.getInstance().createNotification(getApplicationContext(), ENotificationType.SAVING_RUN);
                                exitDialog = new MaterialDialog.Builder(TrackingActivity.this)
                                        .setAnimation(R.raw.success)
                                        .setTitle(getString(R.string.saving_run_notification_title))
                                        .setMessage(getString(R.string.saving_run_notification_description))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(android.R.string.ok), (dialogInterface, which) -> {
                                            exitDialog.dismiss();
                                            finish();
                                        })
                                        .build();
                                exitDialog.show();
                            }

                            @Override
                            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                                Log.d(TAG, "Error");
                            }
                        });
                    })
                    .setNegativeButton(getString(R.string.stop_tracking_alert_cancel), R.drawable.ic_close, (dialogInterface, which) ->
                            dialogInterface.dismiss())
                    .build();
            exitDialog.show();
        });

        binding.mapViewButton.setOnClickListener(view -> {
            if (isMapViewSelected) {
                showTrackingDataFragment();
            } else {
                onBackPressed();
            }
            isMapViewSelected = !isMapViewSelected;
        });
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.trackingFragment, fragment)
                .commit();
    }

    private void showTrackingDataFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .add(R.id.trackingFragment, new TrackingFragment())
                .addToBackStack("tracking")
                .commit();
    }
}