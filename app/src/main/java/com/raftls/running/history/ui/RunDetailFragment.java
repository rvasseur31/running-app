package com.raftls.running.history.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.raftls.running.R;
import com.raftls.running.app.fragment.BaseToolbarFragment;
import com.raftls.running.app.models.ApiResponse;
import com.raftls.running.app.models.ResponseError;
import com.raftls.running.app.utils.DateUtils;
import com.raftls.running.app.utils.Utils;
import com.raftls.running.databinding.FragmentRunDetailBinding;
import com.raftls.running.history.events.DeleteRun;
import com.raftls.running.history.services.HistoryService;
import com.raftls.running.map.MapUtils;
import com.raftls.running.tracking.models.geojson.Geometry;
import com.raftls.running.tracking.models.geojson.Run;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

public class RunDetailFragment extends BaseToolbarFragment implements OnMapReadyCallback {

    private static final String ARG_RUN = "ARG_RUN";
    private static final Gson gson = new Gson();

    private FragmentRunDetailBinding binding;
    private Run run;
    private Geometry runGeometry;
    private final HistoryService historyService;

    public RunDetailFragment() {
        historyService = HistoryService.getInstance();
        // Required empty public constructor
    }

    public static RunDetailFragment newInstance(Run run) {
        RunDetailFragment fragment = new RunDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RUN, gson.toJson(run));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            run = gson.fromJson(getArguments().getString(ARG_RUN), Run.class);
            runGeometry = run.getRun().getFeatures().get(0).getGeometry();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRunDetailBinding.inflate(inflater, container, false);
        binding.tvDistance.setText(getString(R.string.n_km, String.valueOf(Utils.round(run.getDistance() / 1000, 1))));
        binding.tvElevationGain.setText(getString(R.string.value_elevation_gain,
                String.valueOf(Utils.round(runGeometry.getElevationGain(), 1))));
        binding.tvMaxAltitude.setText(getString(R.string.value_max_altitude,
                String.valueOf(Utils.round(runGeometry.getMaxAltitude(), 1))));
        binding.tvTime.setText(DateUtils.getDurationToString(container.getContext(), run.getDuration()));
        binding.tvAverageSpeed.setText(getString(R.string.value_max_speed,
                String.valueOf(Utils.round(run.getAverageSpeed(), 1))));
        binding.tvMaxSpeed.setText(getString(R.string.value_max_speed,
                String.valueOf(Utils.round(run.getMaxSpeed(), 1))));
        binding.mapView.onCreate(null);
        binding.mapView.getMapAsync(this);
        binding.btnBack.setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        binding.btnMore.setOnClickListener(view -> showMenu(view, R.menu.detail_run_menu));
        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            MapUtils.drawLines(mapboxMap, run.getRun());
            Location firstLocation = runGeometry.getFirstPosition();

            CameraPosition location = new CameraPosition.Builder()
                    .target(new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude()))
                    .zoom(13f)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(location), 1000);
        });
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
    public void onMessageEvent(DeleteRun event) {
        historyService.removeRun(run.getId(), new ApiResponse<Void>() {
            @Override
            public void success(Void response) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void failure(ResponseError response) {

            }
        });
    }
}