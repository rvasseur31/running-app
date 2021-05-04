package com.raftls.running.history.models;

import com.raftls.running.history.adapters.HistoryItem;

public class DeletedElement {
    private final HistoryItem item;

    private final int itemPosition;

    public DeletedElement(HistoryItem item, int itemPosition) {
        this.item = item;
        this.itemPosition = itemPosition;
    }

    public HistoryItem getItem() {
        return item;
    }

    public int getItemPosition() {
        return itemPosition;
    }
}
