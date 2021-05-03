package com.raftls.running.history.adapters;

import android.content.Context;
import android.location.Location;
import android.view.View;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.raftls.running.R;
import com.raftls.running.app.utils.DateUtils;
import com.raftls.running.history.adapters.viewHolders.HistoryViewHolder;
import com.raftls.running.map.MapUtils;
import com.raftls.running.tracking.models.geojson.Run;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class HistoryItem extends AbstractFlexibleItem<HistoryViewHolder> {
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

    @Override
    public int getLayoutRes() {
        return R.layout.item_history;
    }

    @Override
    public HistoryViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new HistoryViewHolder(view, adapter, run);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, HistoryViewHolder holder,
                               int position,
                               List<Object> payloads) {
        holder.title.setText("My run");
        holder.dateTime.setText(run.getStartingTime().toString());
        holder.distance.setText(context.getString(R.string.n_km, String.valueOf(run.getDistance() / 1000)));
        holder.elevationGain.setText(context.getString(R.string.value_elevation_gain,
                String.valueOf(run.getRun().getFeatures().get(0).getGeometry().getElevationGain())));
        holder.time.setText(DateUtils.getDurationToString(context, run.getDuration()));
    }
}