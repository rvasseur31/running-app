package com.raftls.running.history.adapters.viewHolders;

import android.view.View;
import android.widget.TextView;

import com.raftls.running.R;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class HistoryViewHolder extends FlexibleViewHolder {

    public TextView mTitle;

    public HistoryViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        mTitle = view.findViewById(R.id.title);
    }
}
