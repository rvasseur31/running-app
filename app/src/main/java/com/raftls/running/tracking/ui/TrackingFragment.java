package com.raftls.running.tracking.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.raftls.running.app.Utils;
import com.raftls.running.databinding.FragmentTrackingBinding;
import com.raftls.running.tracking.events.StartTrackingEvent;
import com.raftls.running.tracking.events.PauseTrackingEvent;
import com.raftls.running.tracking.models.ETrackingState;
import com.raftls.running.tracking.services.TrackingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class TrackingFragment extends Fragment {

    private final Timer timer = new Timer();
    private FragmentTrackingBinding binding;
    private final TrackingService trackingService = TrackingService.getInstance();

    public TrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startChronometer(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startChronometer(StartTrackingEvent event) {
        if (trackingService.getChronometer().getPauseBaseTime() == 0L) {
            binding.chronometer.setBase(SystemClock.elapsedRealtime() - trackingService.getChronometer().getChronometerTime());
        } else {
            binding.chronometer.setBase(SystemClock.elapsedRealtime() - trackingService.getTimeWhenPause());
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showTrackingData();
            }
        }, 0, 10000);
        if (trackingService.trackingState == ETrackingState.RUNNING) {
            binding.chronometer.start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopChronometer(PauseTrackingEvent event) {
        binding.chronometer.stop();
        trackingService.setTimeWhenPause(SystemClock.elapsedRealtime() - binding.chronometer.getBase());
        timer.cancel();
        timer.purge();

    }

    private void showTrackingData() {
        if (getActivity() != null) {
            // Must use the uiThread
            getActivity().runOnUiThread(() -> {
                binding.tvDistance.setText(String.valueOf(Utils.round(trackingService.getDistance() / 1000, 1)));
                binding.tvAverageSpeed.setText(String.valueOf(Utils.round(trackingService.getAverageSpeed(), 2)));
            });
        }
    }
}