package com.raftls.running.tracking.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.raftls.running.R;
import com.raftls.running.databinding.ActivityTrackingBinding;
import com.raftls.running.network.ApiClient;
import com.raftls.running.notification.models.ENotificationType;
import com.raftls.running.notification.services.NotificationService;
import com.raftls.running.tracking.events.LocationPermissionGrantedEvent;
import com.raftls.running.tracking.models.ETrackingState;
import com.raftls.running.tracking.services.TrackingService;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends AppCompatActivity implements PermissionsListener {

    private static final String TAG = TrackingActivity.class.getSimpleName();
    private boolean isMapViewSelected = true;
    private ActivityTrackingBinding binding;
    private final TrackingService trackingService = TrackingService.getInstance();
    private final MapFragment mapFragment = new MapFragment();
    private MaterialDialog exitDialog;
    private MaterialDialog locationPermissionDialog;
    private PermissionsManager permissionsManager;

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
                if (!trackingService.startTracking(this, mapFragment.getMapboxMap())) {
                    permissionsManager.requestLocationPermissions(this);
                    return;
                }
                binding.startTracking.setIcon(ContextCompat.getDrawable(TrackingActivity.this, R.drawable.ic_pause));
                binding.mapViewButton.setVisibility(View.VISIBLE);
                binding.stopTracking.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationPermissionDialog != null) {
            locationPermissionDialog.dismiss();
        }
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            EventBus.getDefault().post(new LocationPermissionGrantedEvent());
        } else {
            locationPermissionDialog = new MaterialDialog.Builder(this)
                    .setTitle(getString(R.string.user_location_permission_not_granted_title))
                    .setMessage(getString(R.string.user_location_permission_not_granted_description))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.open_settings), R.drawable.ic_open_in_new, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton(getString(R.string.exit), R.drawable.ic_close, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        finish();
                    })
                    .build();
            locationPermissionDialog.show();
        }
    }
}