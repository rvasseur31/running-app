package com.raftls.running.tracking.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.raftls.running.tracking.events.PauseTrackingEvent;
import com.raftls.running.tracking.events.StartTrackingEvent;
import com.raftls.running.tracking.models.ETrackingState;
import com.raftls.running.tracking.models.geojson.FeatureCollection;
import com.raftls.running.tracking.models.geojson.Run;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import br.com.helpdev.chronometerlib.Chronometer;
import br.com.helpdev.chronometerlib.ChronometerManager;

import static com.mapbox.geojson.constants.GeoJsonConstants.MAX_LATITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MAX_LONGITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MIN_LATITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MIN_LONGITUDE;

public class TrackingService {
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static final String TAG = TrackingService.class.getSimpleName();

    public ETrackingState trackingState = ETrackingState.STOPPED;
    private static TrackingService instance;
    private FeatureCollection currentRun;

    private LocationEngine locationEngine;
    private LocationEngineCallback<LocationEngineResult> locationEngineCallback;
    private float distance = 0;
    private ChronometerManager chronometer;

    public static TrackingService getInstance() {
        if (instance == null) {
            instance = new TrackingService();
        }
        return instance;
    }

    private TrackingService() {
        createRun();
    }

    public boolean startTracking(Context context, @Nullable MapboxMap mapboxMap) {
        trackingState = ETrackingState.RUNNING;
        if (chronometer == null) {
            chronometer = new ChronometerManager(new Chronometer(), System::currentTimeMillis);
        }
        chronometer.start();
        EventBus.getDefault().post(new StartTrackingEvent());
        return startLocationTracking(context, mapboxMap);
    }

    public void pauseTracking() {
        trackingState = ETrackingState.PAUSED;
        chronometer.pause();
        EventBus.getDefault().post(new PauseTrackingEvent());
        stopLocationTracking();
    }

    public void resetTracking() {
        trackingState = ETrackingState.STOPPED;
        createRun();
    }

    public boolean startLocationTracking(Context context, @Nullable MapboxMap mapboxMap) {
        locationEngine = LocationEngineProvider.getBestLocationEngine(context);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        locationEngineCallback = new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                Location lastSavedLocation = currentRun.getFeatures().get(0).getGeometry().getLastPosition();

                if (location == null) {
                    return;
                }
                if (lastSavedLocation != null) {
                    float distanceWithLastSavedLocation = lastSavedLocation.distanceTo(location);
                    if (distanceWithLastSavedLocation < 5) {
                        return;
                    } else {
                        distance += distanceWithLastSavedLocation;
                    }
                }

                addPosition(location.getLongitude(), location.getLatitude(), location.getAltitude());
                if (mapboxMap != null && result.getLastLocation() != null) {
                    mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        };

        locationEngine.requestLocationUpdates(request, locationEngineCallback, Looper.getMainLooper());
        locationEngine.getLastLocation(locationEngineCallback);
        return true;
    }

    public void stopLocationTracking() {
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(locationEngineCallback);
        }
    }

    public void addPosition(@FloatRange(from = MIN_LONGITUDE, to = MAX_LONGITUDE) double longitude,
                            @FloatRange(from = MIN_LATITUDE, to = MAX_LATITUDE) double latitude,
                            double altitude) {
        currentRun.getFeatures().get(0).getGeometry().addPosition(longitude, latitude, altitude);
    }

    private void createRun() {
        this.currentRun = new FeatureCollection();
    }

    public Run getCurrentRun() {
        return new Run(getAverageSpeed(), new Date(chronometer.getCurrentBase()), getDuration(), distance, currentRun);
    }

    public float getDistance() {
        return distance;
    }

    public ChronometerManager getChronometer() {
        return chronometer;
    }

    public long getDuration() {
        return chronometer.getChronometerTime() - chronometer.getPauseBaseTime();
    }

    public float getAverageSpeed() {
        float hours = getMillisToSeconds(getDuration());
        return (5f / hours) * 3.6f;
    }

    private float getMillisToSeconds(long millis) {
        return millis / 1000f;
    }


}