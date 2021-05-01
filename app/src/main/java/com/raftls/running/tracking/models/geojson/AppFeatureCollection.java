package com.raftls.running.tracking.models.geojson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AppFeatureCollection {

    private static final String TYPE = "FeatureCollection";

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("features")
    @Expose
    private List<Feature> features = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public AppFeatureCollection() {
        this.type = TYPE;
        this.features = new ArrayList<>();
        features.add(new Feature());
    }

    public AppFeatureCollection(String type, List<Feature> features) {
        this.type = type;
        this.features = features;
    }
}
