package com.raftls.running.tracking.models.geojson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class Feature {

    private static final String TYPE = "Feature";

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private JSONObject properties;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getProperties() {
        return properties;
    }

    public void setProperties(JSONObject properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Feature() {
        this.type = TYPE;
        this.properties = new JSONObject();
        this.geometry = new LineString();
    }
}
