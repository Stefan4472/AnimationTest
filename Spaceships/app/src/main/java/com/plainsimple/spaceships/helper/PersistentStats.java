package com.plainsimple.spaceships.helper;

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
