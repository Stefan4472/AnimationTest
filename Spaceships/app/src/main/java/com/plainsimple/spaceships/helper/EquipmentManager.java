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
    public final static String CANNONS_0_KEY = CannonType.DEFAULT.toString();
    public final static String CANNONS_1_KEY = CannonType.UPGRADE_1.toString();
    public final static String CANNONS_2_KEY = CannonType.UPGRADE_2.toString();
    public final static String CANNONS_3_KEY = CannonType.UPGRADE_3.toString();
    public final static String ROCKET_0_KEY = RocketType.DEFAULT.toString();
    public final static String ROCKET_1_KEY = RocketType.UPGRADE_1.toString();
    public final static String ROCKET_2_KEY = RocketType.UPGRADE_2.toString();
    public final static String ROCKET_3_KEY = RocketType.UPGRADE_3.toString();
    public final static String ARMOR_0_KEY = ArmorType.DEFAULT.toString();
    public final static String ARMOR_1_KEY = ArmorType.UPGRADE_1.toString();
    public final static String ARMOR_2_KEY = ArmorType.UPGRADE_2.toString();
    public final static String ARMOR_3_KEY = ArmorType.UPGRADE_3.toString();
    public final static String COINS_KEY = "COINS";

    // strings used to define the default equipment todo: clean up? find a better way
    // form: String key, equipment type, description, image thumbnail, title, cost, status
    private final static String CANNONS_0 = CANNONS_0_KEY + ":CANNON:A cannon that fires laser rounds:" + R.drawable.cannons_0 + ":Laser Cannon:0:EQUIPPED";
    private final static String CANNONS_1 = CANNONS_1_KEY + ":CANNON:A cannon that fires ion rounds:" + R.drawable.cannons_1 + ":Ion Cannon:100:LOCKED";
    private final static String CANNONS_2 = CANNONS_2_KEY + ":CANNON:A cannon that fires radium rounds:" + R.drawable.cannons_2 + ":Radium Cannon:175:LOCKED";
    private final static String CANNONS_3 = CANNONS_3_KEY + ":CANNON:A cannon that fires plasma rounds:" + R.drawable.cannons_3 + ":Plasma Cannon:350:LOCKED";
    private final static String ROCKET_0 = ROCKET_0_KEY + ":ROCKET:A high-explosive projectile:" + R.drawable.rocket0_overlay + ":Standard Rocket:0:EQUIPPED";
    private final static String ROCKET_1 = ROCKET_1_KEY + ":ROCKET:Launches two powerful explosive cannisters:" + R.drawable.rocket1_overlay + ":Hydrogen Rocket:200:LOCKED";
    private final static String ROCKET_2 = ROCKET_2_KEY + ":ROCKET:Launches two medium-powered rockets:" + R.drawable.rocket2_overlay + ":Iridium Rocket:225:LOCKED";
    private final static String ROCKET_3 = ROCKET_3_KEY + ":ROCKET:6 radioactive and fast missiles launched rapidly:" + R.drawable.rocket3_overlay + ":Plutonium Rocket:300:LOCKED";
    private final static String ARMOR_0 = ARMOR_0_KEY + ":ARMOR:Standard spaceship armor:" + R.drawable.spaceship + ":Standard Armor:0:EQUIPPED";
    private final static String ARMOR_1 = ARMOR_1_KEY + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:100:LOCKED";
    private final static String ARMOR_2 = ARMOR_2_KEY + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:200:LOCKED";
    private final static String ARMOR_3 = ARMOR_3_KEY + ":" + Equipment.Type.ARMOR.toString() + ":Upgraded spaceship armor:" + R.drawable.spaceship + ":Upgraded Armor:250:LOCKED";

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

        equipmentStates.put(CANNONS_0_KEY, new Equipment(data.getString(CANNONS_0_KEY, CANNONS_0)));
        equipmentStates.put(CANNONS_1_KEY, new Equipment(data.getString(CANNONS_1_KEY, CANNONS_1)));
        equipmentStates.put(CANNONS_2_KEY, new Equipment(data.getString(CANNONS_2_KEY, CANNONS_2)));
        equipmentStates.put(CANNONS_3_KEY, new Equipment(data.getString(CANNONS_3_KEY, CANNONS_3)));

        equipmentStates.put(ARMOR_0_KEY, new Equipment(data.getString(ARMOR_0_KEY, ARMOR_0)));
        equipmentStates.put(ARMOR_1_KEY, new Equipment(data.getString(ARMOR_1_KEY, ARMOR_1)));
        equipmentStates.put(ARMOR_2_KEY, new Equipment(data.getString(ARMOR_2_KEY, ARMOR_2)));
        equipmentStates.put(ARMOR_3_KEY, new Equipment(data.getString(ARMOR_3_KEY, ARMOR_3)));

        equipmentStates.put(ROCKET_0_KEY, new Equipment(data.getString(ROCKET_0_KEY, ROCKET_0)));
        equipmentStates.put(ROCKET_1_KEY, new Equipment(data.getString(ROCKET_1_KEY, ROCKET_1)));
        equipmentStates.put(ROCKET_2_KEY, new Equipment(data.getString(ROCKET_2_KEY, ROCKET_2)));
        equipmentStates.put(ROCKET_3_KEY, new Equipment(data.getString(ROCKET_3_KEY, ROCKET_3)));

        coins = data.getInt(COINS_KEY, 0);
        prefEditor = data.edit();
    }

    // returns queried Equipment object
    public Equipment getEquipment(String key) throws IllegalArgumentException {
        if (!equipmentStates.containsKey(key)) {
            throw new IllegalArgumentException("Did not recognize given key (" + key + ")");
        } else {
            Log.d("EquipmentManager.java", "Queried " + key + " and returned " + equipmentStates.get(key));
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
