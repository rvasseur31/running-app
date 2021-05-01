package com.raftls.running.history.adapters;

import android.view.View;

import com.raftls.running.R;
import com.raftls.running.history.adapters.viewHolders.HistoryViewHolder;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class HistoryItem extends AbstractFlexibleItem<HistoryViewHolder> {

    private String id;
    private String title;

    public HistoryItem(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof HistoryItem) {
            HistoryItem inItem = (HistoryItem) inObject;
            return this.id.equals(inItem.id);
        }
        return false;
    }

    /**
     * You should implement also this method if equals() is implemented.
     * This method, if implemented, has several implications that Adapter handles better:
     * - The Hash, increases performance in big list during Update & Filter operations.
     * - You might want to activate stable ids via Constructor for RV, if your id
     *   is unique (read more in the wiki page: "Setting Up Advanced") you will benefit
     *   of the animations also if notifyDataSetChanged() is invoked.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * For the item type we need an int value: the layoutResID is sufficient.
     */
    @Override
    public int getLayoutRes() {
        return R.layout.item_history;
    }

    /**
     * Delegates the creation of the ViewHolder to the user (AutoMap).
     * The inflated view is already provided as well as the Adapter.
     */
    @Override
    public HistoryViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new HistoryViewHolder(view, adapter);
    }

    /**
     * The Adapter and the Payload are provided to perform and get more specific
     * information.
     */
    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, HistoryViewHolder holder,
                               int position,
                               List<Object> payloads) {
        holder.mTitle.setText(title);
    }
}