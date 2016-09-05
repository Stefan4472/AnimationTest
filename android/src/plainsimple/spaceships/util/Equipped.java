package plainsimple.spaceships.util;

import android.content.Context;

import java.util.HashMap;

/**
 * Retrieves/commits unlocks and currently in-use equipment
 */
public class Equipped {

    // possible states
    private static final String EQUIPPED = "EQUIPPED";
    private static final String LOCKED = "LOCKED";
    private static final String UNLOCKED = "UNLOCKED";

    // possible bullet types
    private static final String LASER_BULLET = "LASER_BULLET";
    private static final String ION_BULLET = "ION_BULLET";
    private static final String PLASMA_BULLET = "PLASMA_BULLET";
    private static final String PLUTONIUM_BULLET = "PLUTONIUM_BULLET";

    // possible rocket types (todo)
    private static final String ROCKET_DEFAULT = "ROCKET_DEFAULT";

    // possible armor types (todo)
    private static final String ARMOR_DEFAULT = "ARMOR_DEFAULT";

    // stores states of all equipment after reading from file
    private HashMap<String, String> states;

    // path where equipment data is stored
    public static final String FILE_PATH = "EQUIPMENT_STATES";

    public Equipped(Context context) { // todo: private constructor? only one instance? static?
        String saved_info = FileUtil.readFile(context, FILE_PATH);
        // file hasn't been initialized yet - set default values
        if (saved_info.equals("")) {

        } else {

        }
    }

    private void readValues() {

    }

    private void writeValues() {

    }
}
