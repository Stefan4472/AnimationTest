package com.plainsimple.spaceships.store;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import plainsimple.spaceships.R;

/**
 * Provides query and interface for accessing stored equipment states
 * as well as current number of coins
 */

public class EquipmentManager {

    // equipment id's (equal to getDebugString of their corresponding enum
    // for convenience)
    public final static String CANNONS_0_KEY = CannonType.CANNON_0.toString();
    public final static String CANNONS_1_KEY = CannonType.CANNON_1.toString();
    public final static String CANNONS_2_KEY = CannonType.CANNON_2.toString();
    public final static String CANNONS_3_KEY = CannonType.CANNON_3.toString();
    public final static String ROCKET_0_KEY = RocketType.ROCKET_0.toString();
    public final static String ROCKET_1_KEY = RocketType.ROCKET_1.toString();
    public final static String ROCKET_2_KEY = RocketType.ROCKET_2.toString();
    public final static String ROCKET_3_KEY = RocketType.ROCKET_3.toString();
    public final static String ARMOR_0_KEY = ArmorType.ARMOR_0.toString();
    public final static String ARMOR_1_KEY = ArmorType.ARMOR_1.toString();
    public final static String ARMOR_2_KEY = ArmorType.ARMOR_2.toString();
    public final static String ARMOR_3_KEY = ArmorType.ARMOR_3.toString();
    public final static String COINS_KEY = "COINS";

    // strings used to define the default equipment todo: clean up? find a better way. Descriptions should be in R/string. Cooler, more in-depth descriptions.
    // form: String key, equipment type, description, image thumbnail, title, cost, status
    private final static String CANNONS_0 = CANNONS_0_KEY + ":CANNON:A cannon that fires laser rounds:" + R.drawable.cannons_0 + ":Laser Cannon:0:EQUIPPED";
    private final static String CANNONS_1 = CANNONS_1_KEY + ":CANNON:A cannon that fires ion rounds:" + R.drawable.cannons_1 + ":Ion Cannon:100:LOCKED";
    private final static String CANNONS_2 = CANNONS_2_KEY + ":CANNON:A cannon that fires radium rounds:" + R.drawable.cannons_2 + ":Radium Cannon:175:LOCKED";
    private final static String CANNONS_3 = CANNONS_3_KEY + ":CANNON:A cannon that fires plasma rounds:" + R.drawable.cannons_3 + ":Plasma Cannon:350:LOCKED";
    private final static String ROCKET_0 = ROCKET_0_KEY + ":ROCKET:A high-explosive projectile:" + R.drawable.rocket0_overlay + ":Standard Rocket:0:EQUIPPED";
    private final static String ROCKET_1 = ROCKET_1_KEY + ":ROCKET:Launches two powerful explosive cannisters:" + R.drawable.rocket1_overlay + ":Hydrogen Rocket:200:LOCKED";
    private final static String ROCKET_2 = ROCKET_2_KEY + ":ROCKET:Launches two medium-powered rockets:" + R.drawable.rocket2_overlay + ":Iridium Rocket:225:LOCKED";
    private final static String ROCKET_3 = ROCKET_3_KEY + ":ROCKET:6 radioactive and fast missiles launched rapidly:" + R.drawable.rocket3_overlay + ":Plutonium Rocket:300:LOCKED";
    private final static String ARMOR_0 = ARMOR_0_KEY + ":ARMOR:Standard spaceship armor. Steel hull and frame. 30hp. Capable of withstanding light damage.:" + R.drawable.spaceship + ":Standard Armor:0:EQUIPPED";
    private final static String ARMOR_1 = ARMOR_1_KEY + ":ARMOR:Upgraded spaceship armor. A blend of majority steel with chrome for added resilience. 40hp.:" + R.drawable.spaceship + ":Chrome Armor:100:LOCKED";
    private final static String ARMOR_2 = ARMOR_2_KEY + ":ARMOR:Doubly-upgraded spaceship armor. Blended Tungsten can take a beating. Known for it's top quality marks. 60hp.:" + R.drawable.spaceship + ":Tungsten Armor:200:LOCKED";
    private final static String ARMOR_3 = ARMOR_3_KEY + ":ARMOR:Maxiumum upgraded spaceship armor. Majority Inconel smelted with Advanced Steel for an incredibly heavyweight and resilient shield. 85 hp.:" + R.drawable.spaceship + ":Inconel Armor:300:LOCKED";

    // file key where data is stored
    private static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.EQUIPMENT_FILE_KEY";
    // maps key to default String value
    private static final HashMap<String, String> defaultVals = getDefaultVals();
    // stores Equipment objects as they're read in from Preferences
    private static HashMap<String, Equipment> equipmentCache = new HashMap<>();
    // handle to SharedPreferences. MUST BE INITIALIZED USING init()!
    private static SharedPreferences data;

    private EquipmentManager() {

    }

    // use Context to get handle to SharedPreferences
    public static void init(Context context) {
        if (data == null) {
            data = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        }
    }

    // returns queried Equipment object
    public static Equipment retrieve(String key) throws IllegalArgumentException {
        // throw exception if key is not recognized
        if (!defaultVals.containsKey(key)) {
            throw new IllegalArgumentException("Did not recognize given key \"" + key + "\"");
        } else if (equipmentCache.containsKey(key)) { // check cache to see if Equipment has already been loaded
            return equipmentCache.get(key);
        } else { // load Equipment from SharedPreferences, add it to cache, and return
            Equipment loaded = Equipment.fromString(data.getString(key, defaultVals.get(key)));
            equipmentCache.put(key, loaded);
            return loaded;
        }
    }

    // modifies the specified Equipment object and updates SharedPreferences
    private static void modify(String key, Equipment modified) throws IllegalArgumentException {
        if (!defaultVals.containsKey(key)) {
            throw new IllegalArgumentException("Did not recognize given key '" + key + "'");
        } else {
            equipmentCache.put(key, modified);
            data.edit().putString(key, modified.toString()).commit();
        }
    }

    // returns the number of coins available
    public static int getCoins() {
        return data.getInt(COINS_KEY, 0);
    }

    // withdraws coins from coin balance. toSpend must be less than available balance
    public static void spendCoins(int toSpend) throws IllegalStateException {
        if (toSpend > getCoins()) {
            throw new IllegalStateException("Tried to spend more coins than are available");
        } else {
            // upgrade tracked statistics
//            lifeTimeGameStats.incrementCoinsSpent(toSpend);
//            lifeTimeGameStats.incrementUpgradesBought();
//             update coins
//            coins -= toSpend;
//            prefEditor.putInt(COINS_KEY, coins);
//            prefEditor.commit();
        }
    }

    // adds specified number of coins to balance
    public static void addCoins(int toAdd) throws IllegalArgumentException {
        if (toAdd < 0) {
            throw new IllegalArgumentException("Cannot add negative coin value");
        } else {
            data.edit().putInt(COINS_KEY, getCoins() + toAdd).commit();
        }
    }

    // sets previously equipped item of this type to UNLOCKED and sets
    // the specified equipment to EQUIPPED
    public static void equip(String toEquipId) throws IllegalArgumentException {
        if (!defaultVals.containsKey(toEquipId)) {
            throw new IllegalArgumentException("Key '" + toEquipId + "' not recognized");
        } else {
            Equipment to_equip = retrieve(toEquipId);
            Equipment.Type type = to_equip.getType();
            Equipment e;
            for (String key : defaultVals.keySet()) {
                e = retrieve(key);
                if (e.getType().equals(type) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                    modify(e.getId(), e.setStatus(Equipment.Status.UNLOCKED));
                }
            }
            modify(toEquipId, to_equip.setStatus(Equipment.Status.EQUIPPED));
        }
    }

    // sets the specified Equipment to "UNLOCKED"
    public static void buy(String toBuyId) throws IllegalArgumentException {
        if (!defaultVals.containsKey(toBuyId)) {
            throw new IllegalArgumentException("Key '" + toBuyId + "' not recognized");
        } else {
            modify(toBuyId, equipmentCache.get(toBuyId).setStatus(Equipment.Status.UNLOCKED));
        }
    }

    // returns CannonType equipped
    public static CannonType getEquippedCannon() { // todo: store current equipped types in equipmentCache?
        Equipment e;
        // load each Equipment object and check if it meets the criteria
        for (String key : defaultVals.keySet()) {
            e = retrieve(key);
            if (e.getType().equals(Equipment.Type.CANNON) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return CannonType.valueOf(e.getId());
            }
        }
        return null;
    }

    // returns RocketType equipped
    public static RocketType getEquippedRocket() {
        Equipment e;
        for (String key : defaultVals.keySet()) {
            e = retrieve(key);
            if (e.getType().equals(Equipment.Type.ROCKET) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return RocketType.valueOf(e.getId());
            }
        }
        return null;
    }

    public static ArmorType getEquippedArmor() {
        Equipment e;
        for (String key : defaultVals.keySet()) {
            e = retrieve(key);
            if (e.getType().equals(Equipment.Type.ARMOR) && e.getStatus().equals(Equipment.Status.EQUIPPED)) {
                return ArmorType.valueOf(e.getId());
            }
        }
        return null;
    }

    // creates and returns mapping of equipment keys to default String values
    private static HashMap<String, String> getDefaultVals() {
        HashMap<String, String> map = new HashMap<>();
         map.put(CANNONS_0_KEY, CANNONS_0);
         map.put(CANNONS_1_KEY, CANNONS_1);
         map.put(CANNONS_2_KEY, CANNONS_2);
         map.put(CANNONS_3_KEY, CANNONS_3);
         map.put(ROCKET_0_KEY, ROCKET_0);
         map.put(ROCKET_1_KEY, ROCKET_1);
         map.put(ROCKET_2_KEY, ROCKET_2);
         map.put(ROCKET_3_KEY, ROCKET_3);
         map.put(ARMOR_0_KEY, ARMOR_0);
         map.put(ARMOR_1_KEY, ARMOR_1);
         map.put(ARMOR_2_KEY, ARMOR_2);
         map.put(ARMOR_3_KEY, ARMOR_3);
         return map;
    }
}