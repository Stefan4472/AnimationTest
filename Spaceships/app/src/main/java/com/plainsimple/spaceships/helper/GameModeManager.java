package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.plainsimple.spaceships.view.GameView;

import java.util.HashMap;

/**
 * Manages persistent storage of GameMode objects in SharedPreferences as well as retrieval of
 * queried GameMode objects.
 */

public class GameModeManager {

    // file key where data is stored
    private static final String PREFERENCES_FILE_KEY = "com.plainsimple.spaceships.GAMEMODE_FILE_KEY";

    // available GameModes
    public static final String ENDLESS_0 = "ENDLESS_0"; // todo: efficiency?
    public static final String ENDLESS_1 = "ENDLESS_1";
    public static final String ENDLESS_2 = "ENDLESS_2";
    public static final String CAMPAIGN_0 = "CAMPAIGN_0";

    // todo: better way? where to define them?
    // String defining ENDLESS_0 GameMode
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

    private static final HashMap<String, String> defaultStrings = getDefaultStrings();

    // mapping of GameMode key constants with actual content
    private static HashMap<String, String> getDefaultStrings() {
        HashMap<String, String> defaults = new HashMap<>();
        defaults.put(ENDLESS_0, ENDLESS_0_STR);
        defaults.put(ENDLESS_1, ENDLESS_1_STR);
        defaults.put(ENDLESS_2, ENDLESS_2_STR);
        defaults.put(CAMPAIGN_0, CAMPAIGN_0_STR);
        return defaults;
    }

    // looks up the given gameModeKey in the Shared Preferences file. Parses the String and returns
    // the GameMode object resulting from it. The keys must be one of the "available GameModes"
    // defined above. Throws IllegalArgumentException if key is invalid.
    public static GameMode retrieve(Context context, String gameModeKey) throws IllegalArgumentException {
        Log.d("GameModeManager", "Retrieving " + gameModeKey);
        SharedPreferences data = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        if (defaultStrings.containsKey(gameModeKey)) {
            Log.d("GameModeManager", "GameModeKey is valid, returns " + defaultStrings.get(gameModeKey));
            return GameMode.fromString(data.getString(gameModeKey, defaultStrings.get(gameModeKey)));
        } else {
            throw new IllegalArgumentException("Unrecognized key (\"" + gameModeKey + "\")");
        }
    }

    // makes sure the given gameModeKey is valid. Calls gameMode's toString() method and stores it
    // under the given key in the Preferences file
    public static void put(Context context, String gameModeKey, GameMode gameMode) throws IllegalArgumentException {
        // get a handle to correct SharedPreferences fiel
        SharedPreferences data = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        if (defaultStrings.containsKey(gameModeKey)) {
            data.edit().putString(gameModeKey, gameMode.toString()).commit();
        } else {
            throw new IllegalArgumentException("Unrecognized key (\"" + gameModeKey + "\")");
        }
    }

    // returns all GameModeKeys in a String array
    public static String[] getGameModeKeys() {
        return defaultStrings.keySet().toArray(new String[defaultStrings.size()]);
    }
}
