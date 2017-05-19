package com.plainsimple.spaceships.store;

import android.util.Log;

/**
 * Stores all information about a piece of Equipment available.
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

    private Equipment() {

    }

    // creates the object using information from the given String, which must be in the correct format.
    // The string should be the toString() of an Equipment object
    public static Equipment fromString(String constructor) throws IllegalArgumentException {
        Equipment constructed = new Equipment();
        String[] values = constructor.split(":");
        try {
            constructed.id = values[0];
            constructed.type = Type.valueOf(values[1]);
            constructed.description = values[2];
            constructed.rDrawableId = Integer.parseInt(values[3]);
            constructed.label = values[4];
            constructed.cost = Integer.parseInt(values[5]);
            constructed.status = Status.valueOf(values[6]);
            return constructed;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Not enough parameters");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing the given String. It should be the toString()" +
                    "of an Equipment object");
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
