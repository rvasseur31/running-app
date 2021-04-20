package com.raftls.running.tracking.services;

import android.util.Log;

import androidx.annotation.FloatRange;

import com.google.gson.Gson;
import com.raftls.running.tracking.models.ETrackingState;
import com.raftls.running.tracking.models.geojson.FeatureCollection;

import static com.mapbox.geojson.constants.GeoJsonConstants.MAX_LATITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MAX_LONGITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MIN_LATITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MIN_LONGITUDE;

public class TrackingService {
    private static final String TAG = TrackingService.class.getSimpleName();
    private ETrackingState trackingState = ETrackingState.STOPPED;
    private static TrackingService instance;
    private FeatureCollection currentRun;

    public static TrackingService getInstance() {
        if (instance == null) {
            instance = new TrackingService();
        }
        return instance;
    }

    private TrackingService() {
        createRun();
    }

    public void startTracking() {
        trackingState = ETrackingState.RUNNING;
    }

    public void pauseTracking() {
        trackingState = ETrackingState.PAUSED;
    }

    public void resetTracking() {
        trackingState = ETrackingState.STOPPED;
        createRun();
    }

    public void addPosition(@FloatRange(from = MIN_LONGITUDE, to = MAX_LONGITUDE) double longitude,
                       @FloatRange(from = MIN_LATITUDE, to = MAX_LATITUDE) double latitude,
                       double altitude) {
        currentRun.getFeatures().get(0).getGeometry().addPosition(longitude, latitude, altitude);
    }

    public void sendRun() {
        String run = new Gson().toJson(currentRun);
        Log.d(TAG, run);
    }

    private void createRun() {
        this.currentRun = new FeatureCollection();
    }
}