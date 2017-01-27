package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import plainsimple.spaceships.R;

/**
 * Provides query and interface for accessing stored equipment states
 */

public class EquipmentManager {

    // equipment id's
    public final static String LASER_KEY = "LASER_CANNON";
    public final static String ION_KEY = "ION_CANNON";
    public final static String PLASMA_KEY = "PLASMA_CANNON";
    public final static String PLUTONIUM_KEY = "PLUTONIUM_CANNON";
    public final static String ROCKET_KEY = "DEF_ROCKET";
    public final static String ARMOR_KEY = "DEF_ARMOR";

    // strings used to define the default equipment
    private final static String LASER_CANNON = "LASER_CANNON:CANNON:A cannon that fires laser rounds:" + R.drawable.spaceship + ":Laser Cannon:0:EQUIPPED";
    private final static String ION_CANNON = "ION_CANNON:CANNON:A cannon that fires ion rounds:" + R.drawable.spaceship + ":Ion Cannon:100:LOCKED";
    private final static String PLASMA_CANNON = "PLASMA_CANNON:CANNON:A cannon that fires plasma rounds:" + R.drawable.spaceship + ":Plasma Cannon:175:LOCKED";
    private final static String PLUTONIUM_CANNON = "PLUTONIUM_CANNON:CANNON:A cannon that fires plutonium rounds:" + R.drawable.spaceship + ":Plutonium Cannon:350:LOCKED";
    private final static String ROCKET = "ROCKET:ROCKET:A high-explosive projectile:" + R.drawable.spaceship + ":Standard Rocket:0:EQUIPPED";
    private final static String ARMOR = "ARMOR:ARMOR:Standard spaceship armor:" + R.drawable.spaceship + ":Standard Armor:0:EQUIPPED";

    // file key where data is stored
    public static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.EQUIPMENT_FILE_KEY";

    // stores Equipment objects for lookup
    private HashMap<String, Equipment> equipmentStates = new HashMap<>();
    // used to write changes to SharedPreferences
    private SharedPreferences.Editor prefEditor;

    public EquipmentManager(Context context) {
        // reads in Equipment data from SharedPreferences and construct Equipment objects. Populate the hashmap
        SharedPreferences data = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        equipmentStates.put(LASER_KEY, new Equipment(data.getString(LASER_KEY, LASER_CANNON)));
        equipmentStates.put(ION_KEY, new Equipment(data.getString(ION_KEY, ION_CANNON)));
        equipmentStates.put(PLASMA_KEY, new Equipment(data.getString(PLASMA_KEY, PLASMA_CANNON)));
        equipmentStates.put(PLUTONIUM_KEY, new Equipment(data.getString(PLUTONIUM_KEY, PLUTONIUM_CANNON)));
        equipmentStates.put(ARMOR_KEY, new Equipment(data.getString(ARMOR_KEY, ARMOR)));
        equipmentStates.put(ROCKET_KEY, new Equipment(data.getString(ROCKET_KEY, ROCKET)));
        prefEditor = data.edit();
    }

    // returns queried Equipment object
    public Equipment getEquipment(String key) throws IllegalArgumentException {
        if (!equipmentStates.containsKey(key)) {
            throw new IllegalArgumentException("Did not recognize given key (" + key + ")");
        } else {
            return equipmentStates.get(key);
        }
    }

    // modifies the specified Equipment object and updates SharedPreferences
    public void modify(String key, Equipment modified) throws IllegalArgumentException {
        if (!equipmentStates.containsKey(key)) {
            throw new IllegalArgumentException("Did not recognize given key (" + key + ")");
        } else {
            equipmentStates.put(key, modified);
            prefEditor.putString(key, modified.toString());
            prefEditor.apply();
        }
    }

    // returns the specific equipment that is currently equipped
    // given the type to look for
    public Equipment.Type getEquipped(Equipment.Type query) { // todo: this probably won't work
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(query) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return e.getType();
            }
        }
        return null;
    }
}
