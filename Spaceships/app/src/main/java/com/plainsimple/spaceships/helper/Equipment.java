package com.plainsimple.spaceships.helper;

import android.util.Log;

import plainsimple.spaceships.R;

/**
 * Retrieving/Saving/Accessing equipment data
 */

public class Equipment {

    private String id; // todo: rename to enum?
    private Type type;
    private String description;
    private int rDrawableId;
    private String label;
    private int cost;
    private Status status;

    public enum Type {
        CANNON, ROCKET, ARMOR;
    }

    public enum Status {
        EQUIPPED, LOCKED, UNLOCKED;
    }

    // creates the object using information from the given constructor
    // String must include all values
    public Equipment(String constructor) throws IllegalArgumentException {
        String[] values = constructor.split(":");
        try {
            id = values[0];
            type = Type.valueOf(values[1]);
            description = values[2];
            rDrawableId = Integer.parseInt(values[3]);
            label = values[4];
            cost = Integer.parseInt(values[5]);
            status = Status.valueOf(values[6]);
            if (id.equals("ARMOR_1")) {
                Log.d("Equipment.java", "ARMOR_1 has drawable id " + rDrawableId);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Not enough parameters");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error casting one of the values. String is " + constructor);
        }
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getrDrawableId() {
        return rDrawableId;
    }

    public String getLabel() {
        return label;
    }

    public int getCost() {
        return cost;
    }

    public Status getStatus() {
        return status;
    }

    public Equipment setStatus(Status newStatus) {
        status = newStatus;
        return this;
    }

    // returns String representation in a readable format
    public String toString() {
        return id + ":" + type.toString() + ":" + description + ":" + rDrawableId +
                ":" + label + ":" + cost + ":" + status.toString();
    }
}
