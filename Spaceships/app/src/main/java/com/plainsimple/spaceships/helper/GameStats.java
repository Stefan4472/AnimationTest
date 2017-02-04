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

    // keys to data-values in the lifetime game statistics SharedPreference file
    public static final String ALIENS_KILLED = "ALIENS_KILLED";
    public static final String TIME_PLAYED = "TIME_PLAYED"; // todo: add this function
    public static final String DISTANCE_TRAVELED = "DISTANCE_TRAVELED"; // todo: coming soon
    public static final String GAME_SCORE = "GAME_SCORE";
    public static final String COINS_COLLECTED = "COINS_COLLECTED";

    protected HashMap<String, Double> values = new HashMap<>();

    public GameStats() {
        values.put(ALIENS_KILLED, new Double(0));
        values.put(TIME_PLAYED, new Double(0));
        values.put(DISTANCE_TRAVELED, new Double(0));
        values.put(GAME_SCORE, new Double(0));
        values.put(COINS_COLLECTED, new Double(0));
    }

    // sets value of specified key
    public void set(String key, double value) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            values.put(key, value);
        }
    }

    // ads amountAdded to the value of the specified key
    public void addTo(String key, double amountAdded) throws IllegalArgumentException {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("GameStats.java: Key not recognized");
        } else {
            values.put(key, values.get(key) + amountAdded);
        }
    }

    // returns value of given key
    public double get(String key) throws IllegalArgumentException {
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

    @Override
    public String toString() {
        String to_string = "";
        for (String key : values.keySet()) {
            to_string += key + ": " + values.get(key) + "\n";
        }
        return to_string;
    }
}
