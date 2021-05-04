package com.raftls.running.tracking.models.geojson;

import android.location.Location;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.shifter.CoordinateShifterManager;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.geojson.constants.GeoJsonConstants.MAX_LATITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MAX_LONGITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MIN_LATITUDE;
import static com.mapbox.geojson.constants.GeoJsonConstants.MIN_LONGITUDE;

public class Geometry {

    private static final int LONGITUDE = 0;
    private static final int LATITUDE = 1;
    private static final int ALTITUDE = 2;
    private static final String TYPE = "LineString";

    @SerializedName("type")
    @Expose
    protected String type;
    @SerializedName("coordinates")
    @Expose
    protected List<List<Double>> coordinates;

    public Geometry() {
        this.type = TYPE;
        this.coordinates = new ArrayList<>();
    }

    public Geometry(String type, List<List<Double>> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    private List<Double> pointToCoordinates(@FloatRange(from = MIN_LONGITUDE, to = MAX_LONGITUDE) double longitude,
                                            @FloatRange(from = MIN_LATITUDE, to = MAX_LATITUDE) double latitude,
                                            double altitude) {
        List<Double> coordinates;
        if (altitude == 0L) {
            coordinates = CoordinateShifterManager.getCoordinateShifter().shiftLonLat(longitude, latitude);
        } else {
            coordinates = CoordinateShifterManager.getCoordinateShifter().shiftLonLatAlt(longitude, latitude, altitude);
        }
        return coordinates;
    }

    public void addPosition(List<Double> position) {
        coordinates.add(position);
    }

    public void addPosition(@FloatRange(from = MIN_LONGITUDE, to = MAX_LONGITUDE) double longitude,
                            @FloatRange(from = MIN_LATITUDE, to = MAX_LATITUDE) double latitude,
                            double altitude) {
        coordinates.add(pointToCoordinates(longitude, latitude, altitude));
    }

    @Nullable
    public Location getLastPosition() {
        Location location = new Location("");//provider name is unnecessary
        if (coordinates.isEmpty()) {
            return null;
        }
        List<Double> lastPosition = coordinates.get(coordinates.size() - 1);
        location.setLongitude(lastPosition.get(LONGITUDE));
        location.setLatitude(lastPosition.get(LATITUDE));
        return location;
    }

    public Location getFirstPosition() {
        Location location = new Location("");//provider name is unnecessary
        if (coordinates.isEmpty()) {
            return null;
        }
        int FIRST_COORDINATE = 0;
        List<Double> lastPosition = coordinates.get(FIRST_COORDINATE);
        location.setLongitude(lastPosition.get(LONGITUDE));
        location.setLatitude(lastPosition.get(LATITUDE));
        return location;
    }

    public double getElevationGain() {
        double elevationGain = -0;
        List<Double> lastPoint = null;
        for (List<Double> point : coordinates) {
            if (lastPoint != null) {
                try {
                    double elevation = lastPoint.get(ALTITUDE) - point.get(ALTITUDE);
                    if (elevation < 0) {
                        elevationGain += elevation;
                    }

                } catch (Exception exception) {
                    Log.e("", "No altitude");
                }
            }
            lastPoint = point;
        }
        if (elevationGain == -0) {
            return 0;
        }
        return -elevationGain;
    }

    public double getMaxAltitude() {
        double maxAltitude = 0;
        for (List<Double> point : coordinates) {
            try {
                double nextAltitude = point.get(ALTITUDE);
                if (nextAltitude > maxAltitude) {
                    maxAltitude = nextAltitude;
                }
            } catch (Exception exception) {
                Log.e("", "No altitude");
            }
        }
        return maxAltitude;
    }
}
