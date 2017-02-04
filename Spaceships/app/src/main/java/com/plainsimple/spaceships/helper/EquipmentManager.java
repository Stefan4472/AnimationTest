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

    // equipment id's (equal to toString of their corresponding enum
    // for convenience)
    public final static String LASER_KEY = CannonType.LASER.toString();
    public final static String ION_KEY = CannonType.ION.toString();
    public final static String PLASMA_KEY = CannonType.PLASMA.toString();
    public final static String PLUTONIUM_KEY = CannonType.PLUTONIUM.toString();
    public final static String ROCKET_KEY = RocketType.ROCKET.toString();
    public final static String ARMOR0_KEY = ArmorType.DEFAULT.toString();
    public final static String ARMOR1_KEY = ArmorType.ARMOR_1.toString();
    public final static String ARMOR2_KEY = ArmorType.ARMOR_2.toString();
    public final static String ARMOR3_KEY = ArmorType.ARMOR_3.toString();
    public final static String COINS_KEY = "COINS";

    // strings used to define the default equipment todo: clean up?
    private final static String LASER_CANNON = LASER_KEY + ":CANNON:A cannon that fires laser rounds:" + R.drawable.spaceship + ":Laser Cannon:0:EQUIPPED";
    private final static String ION_CANNON = ION_KEY + ":CANNON:A cannon that fires ion rounds:" + R.drawable.spaceship + ":Ion Cannon:100:LOCKED";
    private final static String PLASMA_CANNON = PLASMA_KEY + ":CANNON:A cannon that fires plasma rounds:" + R.drawable.spaceship + ":Plasma Cannon:175:LOCKED";
    private final static String PLUTONIUM_CANNON = PLUTONIUM_KEY + ":CANNON:A cannon that fires plutonium rounds:" + R.drawable.spaceship + ":Plutonium Cannon:350:LOCKED";
    private final static String ROCKET = ROCKET_KEY + ":ROCKET:A high-explosive projectile:" + R.drawable.spaceship + ":Standard Rocket:0:EQUIPPED";
    private final static String ARMOR_0 = ARMOR0_KEY + ":ARMOR:Standard spaceship armor:" + R.drawable.spaceship + ":Standard Armor:0:EQUIPPED";
    private final static String ARMOR_1 = ARMOR1_KEY + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:100:LOCKED";
    private final static String ARMOR_2 = ARMOR2_KEY + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:200:LOCKED";
    private final static String ARMOR_3 = ARMOR3_KEY + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:250:LOCKED";

    // file key where data is stored
    public static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.EQUIPMENT_FILE_KEY";

    // stores Equipment objects for lookup
    private HashMap<String, Equipment> equipmentStates = new HashMap<>();
    // coins available
    private int coins;
    // used to write changes to SharedPreferences
    private SharedPreferences.Editor prefEditor;

    // reads in Equipment data from SharedPreferences and constructs Equipment objects
    // Populates the HashMap
    public EquipmentManager(Context context) {
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
    private void modify(String key, Equipment modified) throws IllegalArgumentException {
        if (!equipmentStates.containsKey(key)) {
            throw new IllegalArgumentException("Did not recognize given key (" + key + ")");
        } else {
            equipmentStates.put(key, modified);
            prefEditor.putString(key, modified.toString());
            prefEditor.commit();
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
            prefEditor.commit();
        }
    }

    // adds specified number of coins to balance
    public void addCoins(int toAdd) {
        coins += toAdd;
        prefEditor.putInt(COINS_KEY, coins);
        prefEditor.commit();
    }

    // returns BulletType equipped
    public CannonType getEquippedCannon() {
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(Equipment.Type.CANNON) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return CannonType.valueOf(e.getId());
            }
        }
        return null;
    }

    // sets previously equipped item of this type to UNLOCKED and sets
    // the specified equipment to EQUIPPED
    public void equip(String toEquipId) throws IllegalArgumentException {
        if (!equipmentStates.containsKey(toEquipId)) {
            throw new IllegalArgumentException("Key " + toEquipId + " was not recognized");
        } else {
            Equipment to_equip = equipmentStates.get(toEquipId);
            Equipment.Type type = to_equip.getType();
            Equipment e;
            for (String key : equipmentStates.keySet()) {
                e = equipmentStates.get(key);
                if (e.getType().equals(type) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                    modify(e.getId(), e.setStatus(Equipment.Status.UNLOCKED));
                }
            }
            modify(toEquipId, to_equip.setStatus(Equipment.Status.EQUIPPED));
        }
    }

    // sets the specified Equipment to "UNLOCKED"
    public void buy(String toBuyId) throws IllegalArgumentException {
        modify(toBuyId, equipmentStates.get(toBuyId).setStatus(Equipment.Status.UNLOCKED));
    }

    // returns RocketType equipped
    public RocketType getEquippedRocket() {
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(Equipment.Type.ROCKET) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return RocketType.valueOf(e.getId());
            }
        }
        return null;
    }

    public ArmorType getEquippedArmor() {
        Equipment e;
        for (String key : equipmentStates.keySet()) {
            e = equipmentStates.get(key);
            if (e.getType().equals(Equipment.Type.ARMOR) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return ArmorType.valueOf(e.getId()); // todo: clean up
            }
        }
        return null;
    }
}
