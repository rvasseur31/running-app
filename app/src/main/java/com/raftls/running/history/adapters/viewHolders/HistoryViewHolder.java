package com.raftls.running.history.adapters.viewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.raftls.running.R;
import com.raftls.running.databinding.FragmentMapBinding;
import com.raftls.running.tracking.models.geojson.Run;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class HistoryViewHolder extends FlexibleViewHolder implements OnMapReadyCallback {

    public TextView title;
    public TextView dateTime;
    public TextView distance;
    public TextView elevationGain;
    public TextView time;
    public MapView mapView;
    public MapboxMap mapboxMap;

    public HistoryViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        title = view.findViewById(R.id.tvTitle);
        dateTime = view.findViewById(R.id.tvDate);
        distance = view.findViewById(R.id.tvDistance);
        elevationGain = view.findViewById(R.id.tvElevationGain);
        time = view.findViewById(R.id.tvTime);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

        });
    }
}
