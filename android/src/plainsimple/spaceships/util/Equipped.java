package plainsimple.spaceships.util;

import android.content.Context;
import android.util.Log;
import plainsimple.spaceships.R;
import plainsimple.spaceships.util.fileio.FileUtil;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Retrieves/commits unlocks and currently in-use equipment
 */
public class Equipped {

    // possible states // todo: refactoring!!! enums? ints?
    public static final String EQUIPPED = "EQUIPPED";
    public static final String LOCKED = "LOCKED";
    public static final String UNLOCKED = "UNLOCKED";

    // possible bullet types
    public static final String LASER_BULLET = "LASER_BULLET";
    public static final String ION_BULLET = "ION_BULLET";
    public static final String PLASMA_BULLET = "PLASMA_BULLET";
    public static final String PLUTONIUM_BULLET = "PLUTONIUM_BULLET";

    // possible rocket types (todo)
    public static final String ROCKET_DEFAULT = "ROCKET_DEFAULT";

    // possible armor types (todo)
    public static final String ARMOR_DEFAULT = "ARMOR_DEFAULT";

    // stores states of all equipment after reading from file
    public HashMap<String, String> states = new HashMap<>();

    // path where equipment data is stored
    public static final String FILE_PATH = "EQUIPMENT_STATES";

    // context used for reading/writing files
    public Context context;

    public Equipped(Context context) { // todo: private constructor? make class static?
        this.context = context;
        String saved_info = FileUtil.readFile(context, FILE_PATH);
        // file hasn't been initialized yet - set default values
        if (saved_info.equals("")) {
            saved_info = context.getString(R.string.default_equipped_states);
            FileUtil.writeFile(context, FILE_PATH, saved_info);
        }
        readStates(saved_info);
    }

    // todo: what if an error occurs?
    // populate the states hashmap with data read from the given String
    private void readStates(String toRead) {
        Log.d("Equipped.java", "Reading\n" + toRead);
        String[] lines = toRead.split(","); // split toRead into an array of lines
        String equipment, val;
        int separator_index;
        try {
            for (int i = 0; i < lines.length; i++) {
                separator_index = lines[i].indexOf(':');
                equipment = lines[i].substring(0, separator_index);
                val = lines[i].substring(separator_index + 1);
                states.put(equipment, val);
            }
        } catch (IndexOutOfBoundsException|NullPointerException e) {
            Log.d("Equipped Class", "Error occurred reading values in from file. Resetting states");
            readStates(context.getString(R.string.default_equipped_states));
        }
    }

    // formats the states hashmap and writes it to the file
    public void writeStates() {
        String formatted = "";
        for (String key : states.keySet()) {
            formatted += key + ":" + states.get(key) + "\n";
        }
        FileUtil.writeFile(context, FILE_PATH, formatted);
        Log.d("Equipped.java", "Writing\n" + formatted);
    }

    public String getState(String key) {
        return states.get(key);
    }

    public void setState(String key, String state) {
        states.put(key, state);
    }

    // returns the currently equipped bullet type
    public BulletType getEquippedBulletType() {
        if (states.get(LASER_BULLET).equals(EQUIPPED)) {
            return BulletType.LASER;
        } else if (states.get(ION_BULLET).equals(EQUIPPED)) {
            return BulletType.ION;
        } else if (states.get(PLASMA_BULLET).equals(EQUIPPED)) {
            return BulletType.PLASMA;
        } else {
            return BulletType.PLUTONIUM;
        }
    }

    // returns the currently equipped rocket type
    public RocketType getEquippedRocketType() {
        return RocketType.ROCKET;
    }
}
