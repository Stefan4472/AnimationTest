package plainsimple.spaceships.util;

import android.content.Context;
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

    public Equipped(Context context) { // todo: private constructor? only one instance? static?
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
        String equipment = "", value = "";
        boolean value_reached = false;
        for (int i = 0; i < toRead.length(); i++) {
            if (!value_reached && toRead.charAt(i) != ':') { // build the equipment string
                equipment += toRead.charAt(i);
            } else if (toRead.charAt(i) == ':') { // equipment string is finished
                value_reached = true;
            } else if (value_reached && toRead.charAt(i) != '\n') { // build the values string
                value += toRead.charAt(i);
            } else if (value_reached && toRead.charAt(i) == '\n') { // values string is finished; reset
                states.put(equipment, value);
                equipment = "";
                value = "";
                value_reached = false;
            }
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
