package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import plainsimple.spaceships.R;

/**
 * Contains keys for retrieving statistics to the preferences file or
 * storing single-use statistics during the run of a single game (in which
 * case the GameStats object should be instantiated and used)
 */

public class GameStats {

    // name of preferences file where statistics are stored
    public static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.STATS_KEY";

    // keys to data-values in the lifetime game statistics SharedPreference file
    public static final String ALIENS_KILLED = "ALIENS_KILLED";
    public static final String GAMES_PLAYED = "GAMES_PLAYED";
    public static final String TIME_PLAYED = "TIME_PLAYED"; // todo: add this function
    public static final String DISTANCE_TRAVELED = "DISTANCE_TRAVELED"; // todo: coming soon
    public static final String HIGH_SCORE = "HIGH_SCORE";
    public static final String COINS_COLLECTED = "COINS_COLLECTED";
    public static final String POINTS_EARNED = "POINTS_EARNED";

    private HashMap<String, Integer> values = new HashMap<>();

    public GameStats() {
        values.put(ALIENS_KILLED, 0);
        values.put(GAMES_PLAYED, 0);
        values.put(TIME_PLAYED, 0);
        values.put(DISTANCE_TRAVELED, 0);
        values.put(HIGH_SCORE, 0);
        values.put(COINS_COLLECTED, 0);
        values.put(POINTS_EARNED, 0);
    }

    // ads amountAdded to the value of the specified key
    public void addTo(String key, int amountAdded) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            values.put(key, values.get(key) + amountAdded);
        }
    }

    // returns value of given key
    public int get(String key) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            return values.get(key);
        }
    }

    // returns key set of values
    public Set<String> getKeySet() {
        return values.keySet();
    }
}
