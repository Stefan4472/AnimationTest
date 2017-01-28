package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

import plainsimple.spaceships.R;

/**
 * Provides query and interface for accessing stored equipment states
 * as well as current number of coins
 */

public class EquipmentManager {

    // equipment id's
    public final static String LASER_KEY = "LASER_CANNON";
    public final static String ION_KEY = "ION_CANNON";
    public final static String PLASMA_KEY = "PLASMA_CANNON";
    public final static String PLUTONIUM_KEY = "PLUTONIUM_CANNON";
    public final static String ROCKET_KEY = "DEF_ROCKET";
    public final static String ARMOR0_KEY = "DEF_ARMOR";
    public final static String ARMOR1_KEY = "ARMOR_1";
    public final static String ARMOR2_KEY = "ARMOR_2";
    public final static String ARMOR3_KEY = "ARMOR_3";
    public final static String COINS_KEY = "COINS";

    // strings used to define the default equipment todo: make id the toString of the BulletType/RocketType/ArmorType enum it represents. clean up
    private final static String LASER_CANNON = "LASER_CANNON:CANNON:A cannon that fires laser rounds:" + R.drawable.spaceship + ":Laser Cannon:0:EQUIPPED";
    private final static String ION_CANNON = "ION_CANNON:CANNON:A cannon that fires ion rounds:" + R.drawable.spaceship + ":Ion Cannon:100:LOCKED";
    private final static String PLASMA_CANNON = "PLASMA_CANNON:CANNON:A cannon that fires plasma rounds:" + R.drawable.spaceship + ":Plasma Cannon:175:LOCKED";
    private final static String PLUTONIUM_CANNON = "PLUTONIUM_CANNON:CANNON:A cannon that fires plutonium rounds:" + R.drawable.spaceship + ":Plutonium Cannon:350:LOCKED";
    private final static String ROCKET = "ROCKET:ROCKET:A high-explosive projectile:" + R.drawable.spaceship + ":Standard Rocket:0:EQUIPPED";
    private final static String ARMOR_0 = ArmorType.DEFAULT.toString() + ":ARMOR:Standard spaceship armor:" + R.drawable.spaceship + ":Standard Armor:0:EQUIPPED";
    private final static String ARMOR_1 = ArmorType.ARMOR_1.toString() + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:100:LOCKED";
    private final static String ARMOR_2 = ArmorType.ARMOR_2.toString() + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:200:LOCKED";
    private final static String ARMOR_3 = ArmorType.ARMOR_3.toString() + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:250:LOCKED";

    // file key where data is stored
    public static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.EQUIPMENT_FILE_KEY";

    // stores Equipment objects for lookup
    private HashMap<String, Equipment> equipmentStates = new HashMap<>();
    // coins available
    private int coins;
    // used to write changes to SharedPreferences
    private SharedPreferences.Editor prefEditor;

    public EquipmentManager(Context context) {
        // reads in Equipment data from SharedPreferences and construct Equipment objects. Populate the hashmap
        SharedPreferences data = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        equipmentStates.put(LASER_KEY, new Equipment(data.getString(LASER_KEY, LASER_CANNON)));
        equipmentStates.put(ION_KEY, new Equipment(data.getString(ION_KEY, ION_CANNON)));
        equipmentStates.put(PLASMA_KEY, new Equipment(data.getString(PLASMA_KEY, PLASMA_CANNON)));
        equipmentStates.put(PLUTONIUM_KEY, new Equipment(data.getString(PLUTONIUM_KEY, PLUTONIUM_CANNON)));
        equipmentStates.put(ARMOR0_KEY, new Equipment(data.getString(ARMOR0_KEY, ARMOR_0)));
        equipmentStates.put(ARMOR1_KEY, new Equipment(data.getString(ARMOR1_KEY, ARMOR_1)));
        equipmentStates.put(ARMOR2_KEY, new Equipment(data.getString(ARMOR2_KEY, ARMOR_2)));
        equipmentStates.put(ARMOR3_KEY, new Equipment(data.getString(ARMOR3_KEY, ARMOR_3)));
        equipmentStates.put(ROCKET_KEY, new Equipment(data.getString(ROCKET_KEY, ROCKET)));
        coins = data.getInt(COINS_KEY, 0);
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

    // returns the number of coins available
    public int getCoins() {
        return coins;
    }

    // withdraws coins from coin balance. toSpend must be less than available balance
    public void spendCoins(int toSpend) throws IllegalStateException {
        if (toSpend > coins) {
            throw new IllegalStateException("Tried to spend more coins that are available");
        } else {
            coins -= toSpend;
            prefEditor.putInt(COINS_KEY, coins);
            prefEditor.apply();
        }
    }

    // adds specified number of coins to balance
    public void addCoins(int toAdd) {
        coins += toAdd;
        prefEditor.putInt(COINS_KEY, coins);
        prefEditor.apply();
    }

    // returns BulletType equipped
    public BulletType getEquippedCannon() {
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(Equipment.Type.CANNON) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return BulletType.stringToBulletType(e.getId());
            }
        }
        return null;
    }

    // returns RocketType equipped
    public RocketType getEquippedRocket() {
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(Equipment.Type.ROCKET) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return RocketType.stringToRocketType(e.getId()); // todo: clean up
            }
        }
        return null;
    }

    public ArmorType getEquippedArmor() {
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(Equipment.Type.ARMOR) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                Log.d("EquipmentManger.java", "value of = " + e.getId());
                return ArmorType.valueOf(e.getId()); // todo: clean up
            }
        }
        return null;
    }
}
