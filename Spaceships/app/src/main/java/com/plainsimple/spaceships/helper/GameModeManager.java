package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;

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
    public static final String ENDLESS_0 = "ENDLESS_0"; // todo: efficiency

    private static final HashMap<String, String> defaultStrings = getDefaultStrings();

    // mapping of GameMode key constants with actual content
    private static HashMap<String, String> getDefaultStrings() {
        HashMap<String, String> defaults = new HashMap<>();
        defaults.put(ENDLESS_0, ENDLESS_0_STR);
        return defaults;
    }

    // looks up the given gameModeKey in the Shared Preferences file. Parses the String and returns
    // the GameMode object resulting from it. The keys must be one of the "available GameModes"
    // defined above. Throws IllegalArgumentException if key is invalid.
    public static GameMode retrieve(Context context, String gameModeKey) throws IllegalArgumentException {
        SharedPreferences data = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        if (defaultStrings.containsKey(gameModeKey)) {
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

    // todo: better way? where to define them?
    // String defining ENDLESS_0 GameMode
    private static final String ENDLESS_0_STR = "Endless:" + GameView.Difficulty.EASY + ":" + 0 + ":" + 2000
            + ":" + 4000 + ":" + 7000 + ":" + 12000 + ":" + 25000 + ":" + "Survive! The farther you go the " +
            "harder it gets and the more coins and points you'll earn!" + ":" + "GENERATE BABY!";
}
