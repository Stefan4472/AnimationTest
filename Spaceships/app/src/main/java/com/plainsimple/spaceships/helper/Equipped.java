package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.util.Log;

import com.plainsimple.spaceships.util.*;

import java.util.HashMap;


import plainsimple.spaceships.R;

/**
 * Stores constants todo: bad practice?
 */
public class Equipped {

    public static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.EQUIPPED_FILE_KEY";
    public static final String STATE_EQUIPPED = "STATE_EQUIPPED";
    public static final String STATE_LOCKED = "STATE_LOCKED";
    public static final String STATE_UNLOCKED = "STATE_UNLOCKED";

    public static final String EQUIPPED_BULLET = "EQUIPPED_BULLET";
    public static final String EQUIPPED_ROCKET = "EQUIPPED_ROCKET";
    public static final String EQUIPPED_ARMOR = "EQUIPPED_ARMOR";

    // possible bullet types
    public static final String LASER_BULLET = "LASER_BULLET";
    public static final String ION_BULLET = "ION_BULLET";
    public static final String PLASMA_BULLET = "PLASMA_BULLET";
    public static final String PLUTONIUM_BULLET = "PLUTONIUM_BULLET";

    // possible rocket types (todo)
    public static final String ROCKET_DEFAULT = "ROCKET_DEFAULT";

    // possible armor types (todo)
    public static final String ARMOR_DEFAULT = "ARMOR_DEFAULT";

    // key to get available coins
    public static final String COINPURSE_KEY = "COINS_AVAILABLE";

    public static BulletType stringToBulletType(String key) {
        switch (key) {
            case LASER_BULLET:
                return BulletType.LASER;
            case ION_BULLET:
                return BulletType.ION;
            case PLASMA_BULLET:
                return BulletType.PLASMA;
            case PLUTONIUM_BULLET:
                return BulletType.PLUTONIUM;
            default:
                throw new IllegalArgumentException("Did not recognize " + key);
        }
    }

    public static RocketType stringToRocketType(String key) {
        switch (key) {
            case ROCKET_DEFAULT:
                return RocketType.ROCKET;
            default:
                throw new IllegalArgumentException("Did not recognize " + key);
        }
    }
}
