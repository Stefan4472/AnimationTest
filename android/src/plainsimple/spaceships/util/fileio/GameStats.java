package plainsimple.spaceships.util.fileio;

import android.content.Context;

import java.util.HashMap;

/**
 * Class for storing and retrieving meta game stats
 */
public class GameStats {

    // key to SharedPreference file containing lifetime game statistics
    private String GAME_STATS_FILE_KEY = "GAME_STATS_FILE_KEY";
    // keys to data-values in the lifetime game statistics SharedPreference file
    private String ALIENS_KILLED = "ALIENS_KILLED";
    private String GAMES_PLAYED = "GAMES_PLAYED";
    //private String TIME_PLAYED = "TIME_PLAYED"; // todo: support more data types
    private String DISTANCE_TRAVELED = "DISTANCE_TRAVELED";
    private String HIGH_SCORE = "HIGH_SCORE";
    private String COINS_COLLECTED = "COINS_COLLECTED";
    private String POINTS_EARNED = "POINTS_EARNED";

    // stores fields as objects, which will be cast
    HashMap<String, Object> objects = new HashMap<>();

    public GameStats(Context context) {

    }

    public Integer getInteger(String key) {
        Object o = objects.get(key);
        if (o == null || !(o instanceof Integer)) {
            return null;
        } else {
            return (Integer) o;
        }
    }

    public void setInteger(String key, Integer integer) {
        objects.put(key, integer);
    }

    // populates hashmap with data from file
    public void read() {

    }

    // writes data to file
    public void write() {

    }
}
