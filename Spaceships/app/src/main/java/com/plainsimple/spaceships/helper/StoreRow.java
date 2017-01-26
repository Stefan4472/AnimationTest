package com.plainsimple.spaceships.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that stores data used to populate ListView elements in StoreActivity
 */

public class StoreRow {

    // row id
    public int id;
    // row label
    public String rowLabel;
    // StoreItems to display in row
    private List<StoreItem> rowItems;

    public StoreRow(int id, String rowLabel) {
        this.id = id;
        this.rowLabel = rowLabel;
        rowItems = new LinkedList<>();
    }

    // adds a StoreItem to the row
    public void addStoreItem(StoreItem toAdd) {
        rowItems.add(toAdd);
    }

    public int getId() {
        return id;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public List<StoreItem> getRowItems() {
        return rowItems;
    }
}
