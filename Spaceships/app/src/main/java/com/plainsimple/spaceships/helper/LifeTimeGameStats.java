package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Tracks lifetime statistics. Saves them in SharedPreferences.
 * Manages updates.
 */

public class LifeTimeGameStats {

    // name of preferences file where statistics are stored
    public static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.STATS_KEY";

    private static final String GAMES_PLAYED = "GAMES_PLAYED";
    private static final String LIFETIME_SCORE = "LIFETIME_SCORE";
    private static final String HIGH_SCORE = "HIGH_SCORE";
    private static final String MOST_COINS = "MOST_COINS"; // todo: some coming soon
    private static final String TOTAL_COINS = "TOTAL_COINS";
    private static final String FARTHEST_FLOWN = "FARTHEST_FLOWN";
    private static final String TOTAL_FLOWN = "TOTAL_FLOWN";
    private static final String TOTAL_TIME_PLAYED = "TOTAL_TIME_PLAYED";
    private static final String LONGEST_GAME = "LONGEST_GAME";
    private static final String MOST_ALIENS_KILLED = "MOST_ALIENS_KILLED";

    // stores data under keys
    private HashMap<String, Double> values = new HashMap<>();
    // all data stored here under PREFRENCES_FILE_KEY
    private SharedPreferences preferences;
    // used to edit preferences
    private SharedPreferences.Editor pEditor;

   public LifeTimeGameStats(Context context) {
       preferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
       pEditor = preferences.edit();
       values.put(GAMES_PLAYED, getFromPrefs(GAMES_PLAYED));
       values.put(LIFETIME_SCORE, getFromPrefs(LIFETIME_SCORE));
       values.put(HIGH_SCORE, getFromPrefs(HIGH_SCORE));
       values.put(MOST_COINS, getFromPrefs(MOST_COINS));
       values.put(FARTHEST_FLOWN, getFromPrefs(FARTHEST_FLOWN));
       values.put(TOTAL_FLOWN, getFromPrefs(TOTAL_FLOWN));
       values.put(TOTAL_TIME_PLAYED, getFromPrefs(TOTAL_TIME_PLAYED));
       values.put(TOTAL_COINS, getFromPrefs(TOTAL_COINS));
       values.put(LONGEST_GAME, getFromPrefs(LONGEST_GAME));
       values.put(MOST_ALIENS_KILLED, getFromPrefs(MOST_ALIENS_KILLED));
   }

    // get data from preferences and convert from long to double
    private double getFromPrefs(String key) {
        return Double.longBitsToDouble(preferences.getLong(key, 0));
    }

    // convert data from double to long and set in preferences (DOES NOT COMMIT)
    private void setToPrefs(String key, double val) {
        pEditor.putLong(key, Double.doubleToLongBits(val));
    }

    // increments value associated with given key in values HashMap
    private void incrementValue(String key, double amount) {
        values.put(key, values.get(key) + amount);
    }

    // updates all values using stats from the given game
    // returns whether this game was a highscore
    public boolean update(GameStats game) {
        boolean high_score = false;
        incrementValue(GAMES_PLAYED, 1);
        incrementValue(LIFETIME_SCORE, game.get(GameStats.GAME_SCORE));
        incrementValue(TOTAL_COINS, game.get(GameStats.COINS_COLLECTED));
        incrementValue(TOTAL_FLOWN, game.get(GameStats.DISTANCE_TRAVELED));
        incrementValue(TOTAL_TIME_PLAYED, game.get(GameStats.TIME_PLAYED));
        if (game.get(GameStats.COINS_COLLECTED) > values.get(MOST_COINS)) {
            values.put(MOST_COINS, game.get(GameStats.COINS_COLLECTED));
        }
        if (game.get(GameStats.DISTANCE_TRAVELED) > values.get(FARTHEST_FLOWN)) {
            values.put(FARTHEST_FLOWN, game.get(GameStats.DISTANCE_TRAVELED));
        }
        if (game.get(GameStats.TIME_PLAYED) > values.get(LONGEST_GAME)) {
            values.put(LONGEST_GAME, game.get(GameStats.TIME_PLAYED));
        }
        if (game.get(GameStats.ALIENS_KILLED) > values.get(MOST_ALIENS_KILLED)) {
            values.put(MOST_ALIENS_KILLED, game.get(GameStats.ALIENS_KILLED));
        }
        if (game.get(GameStats.GAME_SCORE) > values.get(HIGH_SCORE)) {
            values.put(HIGH_SCORE, game.get(GameStats.GAME_SCORE));
            high_score = true;
        }
        // update and commit preferences
        for (String key : values.keySet()) {
            setToPrefs(key, values.get(key));
        }
        pEditor.commit();
        return high_score;
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
