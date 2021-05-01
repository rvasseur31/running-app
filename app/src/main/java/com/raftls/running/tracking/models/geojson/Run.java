package com.raftls.running.tracking.models.geojson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Run {
    @SerializedName("maxSpeed")
    @Expose
    private float maxSpeed;

    @SerializedName("averageSpeed")
    @Expose
    private float averageSpeed;

    @SerializedName("duration")
    @Expose
    private long duration;

    @SerializedName("distance")
    @Expose
    private float distance;

    @SerializedName("startingTime")
    @Expose
    private Date startingTime;

    @SerializedName("runData")
    @Expose
    private AppFeatureCollection run;

    public Run(float averageSpeed, Date startingTime, long duration, float distance, AppFeatureCollection run) {
        this.maxSpeed = 23.5f;
        this.averageSpeed = averageSpeed;
        this.startingTime = startingTime;
        this.duration = duration;
        this.distance = distance;
        this.run = run;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public AppFeatureCollection getRun() {
        return run;
    }

    public void setRun(AppFeatureCollection run) {
        this.run = run;
    }

    public Date getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Date startingTime) {
        this.startingTime = startingTime;
    }
}
