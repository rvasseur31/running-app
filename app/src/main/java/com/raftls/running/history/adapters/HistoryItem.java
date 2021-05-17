package com.raftls.running.history.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mikepenz.fastadapter.binding.AbstractBindingItem;
import com.mikepenz.fastadapter.binding.BindingViewHolder;
import com.mikepenz.fastadapter.swipe.ISwipeable;
import com.raftls.running.R;
import com.raftls.running.app.utils.DateUtils;
import com.raftls.running.app.utils.Utils;
import com.raftls.running.databinding.ItemHistoryBinding;
import com.raftls.running.map.MapUtils;
import com.raftls.running.tracking.models.geojson.Run;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HistoryItem extends AbstractBindingItem<ItemHistoryBinding> implements OnMapReadyCallback, ISwipeable {
    private final Run run;
    private final Context context;

    public HistoryItem(Context context, Run run) {
        Mapbox.getInstance(context, context.getString(R.string.mapbox_access_token));
        this.run = run;
        this.context = context;
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof HistoryItem) {
            HistoryItem inItem = (HistoryItem) inObject;
            return this.run.getId().equals(inItem.run.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return run.getId().hashCode();
    }

    @NotNull
    @Override
    public ItemHistoryBinding createBinding(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
      return ItemHistoryBinding.inflate(layoutInflater, viewGroup, false);
    }

    @Override
    public void bindView(@NotNull BindingViewHolder<ItemHistoryBinding> holder, @NotNull List<?> payloads) {
        super.bindView(holder, payloads);
        holder.getBinding().tvTitle.setText("My run");
        holder.getBinding().tvDate.setText(run.getStartingTime().toString());
        holder.getBinding().tvDistance.setText(context.getString(R.string.n_km, String.valueOf(Utils.round(run.getDistance() / 1000, 1))));
        holder.getBinding().tvElevationGain.setText(context.getString(R.string.value_elevation_gain,
                String.valueOf(Utils.round(run.getRun().getFeatures().get(0).getGeometry().getElevationGain(), 1))));
        holder.getBinding().tvTime.setText(DateUtils.getDurationToString(context, run.getDuration()));
        holder.getBinding().mapView.onCreate(null);
        holder.getBinding().mapView.getMapAsync(this);
    }

    @Override
    public int getType() {
        return R.id.item_history;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            MapUtils.drawLines(mapboxMap, run.getRun());
            Location firstLocation = run.getRun().getFeatures().get(0).getGeometry().getFirstPosition();

            CameraPosition location = new CameraPosition.Builder()
                    .target(new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude()))
                    .zoom(13f)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(location), 1000);
        });
    }

    @Override
    public boolean isSwipeable() {
        return true;
    }

    public Run getRun() {
        return run;
    }
}