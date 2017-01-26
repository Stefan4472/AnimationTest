package com.plainsimple.spaceships.helper;

import plainsimple.spaceships.R;

/**
 * Defines information required to display an item in the store
 */

public class StoreItem {

    // R.id to retrieve thumbnail bitmap
    private int thumbnailId;
    // R.string of label
    private int labelId;
    // state: unlocked? equipped? unequipped?
    private String equippedState;
    // R.string of description
    private int descriptionId;
    // cost of item
    private int cost;

    public StoreItem(int thumbnailId, int labelId, String equippedState, int descriptionId, int cost) {
        this.thumbnailId = thumbnailId;
        this.labelId = labelId;
        this.equippedState = equippedState;
        this.descriptionId = descriptionId;
        this.cost = cost;
    }

    public int getThumbnailId() {
        return thumbnailId;
    }

    public int getLabelId() {
        return labelId;
    }

    public String getEquippedState() {
        return equippedState;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public int getCost() {
        return cost;
    }
}
