package plainsimple.spaceships.util;

import android.content.Context;
import plainsimple.spaceships.sprites.Alien1;
import plainsimple.spaceships.util.fileio.FileUtil;

import java.util.List;

/**
 * Saves game state to file and can re-create game state from file
 */
public class GameSave {

    private String fileName; // todo: save file names are appended to this, allowing multiple save file systems

    // default prefix
    private static final String DEFAULT_SAVE_FILE = "DefaultSave";

    // stores game state information (volume, currently equipped, etc)
    private static final String GAME_STATE_FILE = "GameStateFile";
    // stores serialized aliens
    private static final String ALIENS_FILE = "AliensFile";
    // stores serialized alien bullets
    private static final String ALIEN_BULLETS = "AlienBulletsFile";
    // stores serialized spaceship
    private static final String SPACESHIP = "SpaceshipFile";
    // stores serialized spaceship bullets
    private static final String BULLETS = "BulletsFile";
    // stores serialized spaceship rockets
    private static final String ROCKETS = "RocketsFile";
    // stores serialized coins
    private static final String COINS = "CoinsFile";
    // stores serialized obstacles
    private static final String OBSTACLES = "ObstaclesFile";
    // stores background
    private static final String BACKGROUND = "BackgroundFile";
    // stores tile generator

    public GameSave() {
        fileName = DEFAULT_SAVE_FILE;
    }

    public GameSave(String fileName) {
        this.fileName = fileName;
    }

    public void clearSavedFiles(Context c) { // todo: save them all in a directory of name filename?
        c.deleteFile(fileName + "." + GAME_STATE_FILE);
        c.deleteFile(fileName + "." + ALIENS_FILE);
        c.deleteFile(fileName + "." + ALIEN_BULLETS);
        c.deleteFile(fileName + "." + SPACESHIP);
        c.deleteFile(fileName + "." + BULLETS);
        c.deleteFile(fileName + "." + ROCKETS);
        c.deleteFile(fileName + "." + COINS);
        c.deleteFile(fileName + "." + OBSTACLES);
        c.deleteFile(fileName + "." + BACKGROUND);
    }

    // writes currently stored values to file
    public void saveGameState(Context c) {

    }

    // creates a game state from the read file
    public void load() {

    }

    public boolean saveAliens(Context c, List<Alien1> aliens) {
        return FileUtil.writeObject(c, fileName + "." + ALIENS_FILE, aliens);
    }

    public List<Alien1> loadAliens(Context c) {
        return (List<Alien1>) FileUtil.readObject(c, fileName + "." + ALIENS_FILE);
    }
}
