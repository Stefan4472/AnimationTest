package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.plainsimple.spaceships.view.GameView;

import java.util.HashMap;

/**
 * Manages persistent storage of GameMode objects in SharedPreferences as well as retrieval of
 * queried GameMode objects. Implemented as a Singleton-type object, so only one instance exists.
 */

public class GameModeManager {

    // available GameModes
    public static final String ENDLESS_0 = "ENDLESS_0"; // todo: efficiency? enums?
    public static final String ENDLESS_1 = "ENDLESS_1";
    public static final String ENDLESS_2 = "ENDLESS_2";
    public static final String CAMPAIGN_0 = "CAMPAIGN_0";

    // todo: better way? where to define them?
    // Strings defining default GameMode objects
    private static final String ENDLESS_0_STR = ENDLESS_0 + ":" + "Endless:" + GameView.Difficulty.EASY
            + ":" + 0 + ":" + 2000 + ":" + 4000 + ":" + 7000 + ":" + 12000 + ":" + 25000 + ":" +
            "Survive! The farther you go the harder it gets and the more coins and points you'll earn!"
            + ":" + "GENERATE BABY!";
    private static final String ENDLESS_1_STR = ENDLESS_1 + ":" + "Endless Asteroids:" + GameView.Difficulty.EASY
            + ":" + 0 + ":" + 2000 + ":" + 4000 + ":" + 7000 + ":" + 12000 + ":" + 25000 + ":" +
            "Survive the Astroid storm! The farther you go the harder it gets and the more coins and points you'll earn!"
            + ":" + "GENERATE ASTEROIDS BABY!";
    private static final String ENDLESS_2_STR = ENDLESS_2 + ":" + "Endless Aliens:" + GameView.Difficulty.EASY
            + ":" + 0 + ":" + 1000 + ":" + 3000 + ":" + 5000 + ":" + 9000 + ":" + 20000 + ":" +
            "Survive the Alien Appocalypse! The farther you go the harder it gets and the more coins and points you'll earn!"
            + ":" + "GENERATE ALIENS BABY!";
    private static final String CAMPAIGN_0_STR = CAMPAIGN_0 + ":" + "Campaign Lvl 0:" + GameView.Difficulty.EASY
            + ":" + 0 + ":" + 10000 + ":" + 15000 + ":" + 20000 + ":" + 25000 + ":" + 30000 + ":" +
            "Complete the mission! Survive to the end of the level!" + ":" + "GENERATE WELL-DESIGNED TERRAIN BABY!";

    // file key where prefs is stored
    private static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.GAMEMODE_FILE_KEY";
    // mapping of each key to its default value
    private static final HashMap<String, String> defaultStrings = getDefaultStrings();
    // cache of already-loaded GameModes
    private static HashMap<String, GameMode> gameModeCache = new HashMap<>();
    // SharedPreferences handle
    private static SharedPreferences prefs;

    private GameModeManager() {

    }

    // get a handle to SharedPreferences using given Context. REQUIRED!
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
            Log.d("GameModeManager", "prefs initialized, equals " + prefs);
        } else {
            Log.d("GameModeManager", "Prefs already initialized, equals " + prefs);
        }
    }

    // looks up the given gameModeKey in the cache, and then the Shared Preferences file.
    // The keys must be one of the "available GameModes" defined above. Throws IllegalArgumentException
    // if key is invalid.
    public static GameMode retrieve(String key) throws IllegalArgumentException {
        Log.d("GameModeManager", "Retrieving " + key);
        // check if already present in cache
        if (gameModeCache.containsKey(key)) {
            return gameModeCache.get(key);
        } else if (defaultStrings.containsKey(key)) { // or load it and add to cache
            if (prefs == null) {
                Log.d("GameModeManager", "Soemthing's wrong"); // todo: what broke?
                return GameMode.fromString(defaultStrings.get(ENDLESS_1));
            }
            GameMode loaded = GameMode.fromString(prefs.getString(key, defaultStrings.get(key)));
            gameModeCache.put(key, loaded);
            return loaded;
        } else {
            throw new IllegalArgumentException("Key '" + key + "' not recognized");
        }
    }

    // updates value under specified key with the given GameMode in cache and in SharedPreferences.
    // Throws IllegalArgumentException if key is invalid
    public static void put(String key, GameMode gameMode) throws IllegalArgumentException {
        if (defaultStrings.containsKey(key)) {
            gameModeCache.put(key, gameMode);
            prefs.edit().putString(key, gameMode.toString()).commit();
            Log.d("GameModeManager", "Put " + gameMode + " under key");
        } else {
            throw new IllegalArgumentException("Key '" + key + "' not recognized");
        }
    }

    // returns all GameModeKeys in a String array
    public static String[] getGameModeKeys() {
        return new String[] {
                ENDLESS_0,
                ENDLESS_1,
                ENDLESS_2,
                CAMPAIGN_0
        };
    }

    // maps GameMode key constants to their default value
    private static HashMap<String, String> getDefaultStrings() {
        HashMap<String, String> defaults = new HashMap<>();
        defaults.put(ENDLESS_0, ENDLESS_0_STR);
        defaults.put(ENDLESS_1, ENDLESS_1_STR);
        defaults.put(ENDLESS_2, ENDLESS_2_STR);
        defaults.put(CAMPAIGN_0, CAMPAIGN_0_STR);
        return defaults;
    }
}
