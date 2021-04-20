package com.raftls.running.tracking.models.geojson;

import java.util.ArrayList;

public class LineString extends Geometry {
    private static final String TYPE = "LineString";

    public LineString() {
        super(TYPE, new ArrayList<>(new ArrayList<>()));
    }
}
