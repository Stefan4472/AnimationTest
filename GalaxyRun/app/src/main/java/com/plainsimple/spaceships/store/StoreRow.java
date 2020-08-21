package com.plainsimple.spaceships.store;

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
    private List<Equipment> rowItems;

    public StoreRow(int id, String rowLabel) {
        this.id = id;
        this.rowLabel = rowLabel;
        rowItems = new LinkedList<>();
    }

    // adds a StoreItem to the row
    public void addStoreItem(Equipment toAdd) {
        rowItems.add(toAdd);
    }

    public int getId() {
        return id;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public List<Equipment> getRowItems() {
        return rowItems;
    }

    @Override
    public String toString() {
        String to_string = rowLabel + " row:\n";
        to_string += rowItems.isEmpty() ? "No Items" : "";
        for (Equipment e : rowItems) {
            to_string += e.toString() + "\n";
        }
        return to_string;
    }
}
