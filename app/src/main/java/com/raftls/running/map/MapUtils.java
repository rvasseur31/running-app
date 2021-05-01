package com.raftls.running.map;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.raftls.running.tracking.models.geojson.AppFeatureCollection;

public class MapUtils {
    public static void drawLines(MapboxMap mapboxMap, @NonNull AppFeatureCollection featureCollection) {
        FeatureCollection mapboxFeatureCollection =
                FeatureCollection.fromJson(new Gson().toJson(featureCollection));
        drawLines(mapboxMap, mapboxFeatureCollection);
    }

    public static void drawLines(MapboxMap mapboxMap, @NonNull FeatureCollection featureCollection) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                if (featureCollection.features() != null) {
                    if (featureCollection.features().size() > 0) {
                        GeoJsonSource source = style.getSourceAs("line-source");
                        if (source != null) {
                            source.setGeoJson(featureCollection);
                        } else {
                            style.addSource(new GeoJsonSource("line-source", featureCollection));
                        }

                        if (style.getLayer("linelayer") == null) {
                            style.addLayer(new LineLayer("linelayer", "line-source")
                                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                            PropertyFactory.lineOpacity(.7f),
                                            PropertyFactory.lineWidth(7f),
                                            PropertyFactory.lineColor(Color.parseColor("#3bb2d0"))));
                        }
                    }
                }
            });
        }
    }
}
