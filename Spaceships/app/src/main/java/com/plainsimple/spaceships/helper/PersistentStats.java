package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.util.FileUtil;

import java.io.File;

import plainsimple.spaceships.R;

/**
 * Keeps track of statistics that are kept track of over
 * many rounds of the game, and must be persisted
 */

public class PersistentStats {

    // name of file where statistics are stored
    private static final String FILE_NAME = "LIFETIME_STATISTICS";

    public static PersistentStats lifeStats = new PersistentStats();

    private static final String SAVE_FILE_NAME = "GAME_SAVE_FILE";

    // writes all necessary data to re-create the GameView to a file
    private void createSaveFile() {

    }
    /*/ key to SharedPreference file containing lifetime game statistics
    private String GAME_STATS_FILE_KEY = "GAME_STATS_FILE_KEY";
    // keys to data-values in the lifetime game statistics SharedPreference file
    private String ALIENS_KILLED = "ALIENS_KILLED";
    private String GAMES_PLAYED = "GAMES_PLAYED";
    private String TIME_PLAYED = "TIME_PLAYED"; // todo: add this function
    private String DISTANCE_TRAVELED = "DISTANCE_TRAVELED"; // todo: coming soon
    private String HIGH_SCORE = "HIGH_SCORE";
    private String COINS_COLLECTED = "COINS_COLLECTED";
    private String POINTS_EARNED = "POINTS_EARNED";

    // updates stored lifetime game statistics with values from the current run // todo: move to GameActivity
    // these statistics are stored using SharedPreferences
    public void onGameFinished() {
        SharedPreferences sharedPref = c.getSharedPreferences(GAME_STATS_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt(ALIENS_KILLED, sharedPref.getInt(ALIENS_KILLED, 0) + aliensKilled);
        editor.putInt(GAMES_PLAYED, sharedPref.getInt(GAMES_PLAYED, 0) + 1);
        if (GameActivity.getScore() > sharedPref.getInt(HIGH_SCORE, 0)) {
            editor.putInt(HIGH_SCORE, GameActivity.getScore());
        }
        //editor.putInt(COINS_COLLECTED, sharedPref.getInt(COINS_COLLECTED, 0) + coinsCollected);
        editor.putInt(POINTS_EARNED, sharedPref.getInt(POINTS_EARNED, 0) + GameActivity.getScore());
        editor.commit();
    }*/


    private PersistentStats() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            String data = (String) FileUtil.readObject(file);

        } else {
            FileUtil.writeObject(file, R.string.default_lifetime_stats);
        }
    }

    private class DataField {

    }
}
