package plainsimple.spaceships.util;

import android.content.Context;
import android.util.Log;
import plainsimple.spaceships.R;

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

    // context used for reading/writing files
    public Context context;

    public Equipped(Context context) { // todo: private constructor? only one instance? static?
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
        String[] lines = toRead.split("//n"); // split toRead into an array of lines
        String equipment, val;
        int separator_index;
        try {
            for (int i = 0; i < lines.length; i++) {
                separator_index = lines[i].indexOf(':');
                equipment = lines[i].substring(0, separator_index);
                val = lines[i].substring(separator_index + 1);
                states.put(equipment, val);
            }
        } catch (Exception e) {
            Log.d("Equipped Class", "Error occurred reading values in from file. Resetting states");
            readStates(context.getString(R.string.default_equipped_states));
        }
    }

    // formats the states hashmap and writes it to the file
    private void writeStates() {
        String formatted = "";
        for (String key : states.keySet()) {
            formatted += key + ":" + states.get(key) + "\n";
        }
        FileUtil.writeFile(context, FILE_PATH, formatted);
    }
}
