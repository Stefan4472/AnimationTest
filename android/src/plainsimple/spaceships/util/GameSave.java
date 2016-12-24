package plainsimple.spaceships.util;

import android.content.Context;
import plainsimple.spaceships.sprites.Spaceship;
import plainsimple.spaceships.sprites.Sprite;
import plainsimple.spaceships.util.fileio.FileUtil;

import java.io.File;
import java.util.LinkedList;
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
    private static final String ALIENS = "AliensFile";
    // stores serialized alien bullets
    private static final String ALIEN_BULLETS = "AlienBulletsFile";
    // stores serialized spaceship
    private static final String SPACESHIP = "SpaceshipFile";
    // stores serialized spaceship bullets and rockets
    private static final String BULLETS = "BulletsFile";
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
        c.deleteFile(fileName + "." + ALIENS);
        c.deleteFile(fileName + "." + ALIEN_BULLETS);
        c.deleteFile(fileName + "." + SPACESHIP);
        c.deleteFile(fileName + "." + BULLETS);
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

    public boolean saveAliens(Context c, List<Sprite> aliens) {
        return FileUtil.writeObject(c, fileName + "." + ALIENS, aliens);
    }

    public List<Sprite> loadAliens(Context c) {
        return loadSpriteList(c, fileName + "." + ALIENS);
    }

    public boolean saveAlienBullets(Context c, List<Sprite> alienBullets) {
        return FileUtil.writeObject(c, fileName + "." + ALIEN_BULLETS, alienBullets);
    }

    public List<Sprite> loadAlienBullets(Context c) {
        return loadSpriteList(c, fileName + "." + ALIEN_BULLETS);
    }

    public boolean saveSpaceship(Context c, Spaceship spaceship) {
        return FileUtil.writeObject(c, fileName + "." + SPACESHIP, spaceship);
    }

    public Spaceship loadSpaceship(Context c) {
        String filePath = fileName + "." + SPACESHIP;
        File f = new File(filePath);
        if(f.exists()) {
            return (Spaceship) FileUtil.readObject(c, filePath);
        } else {
            return null;
        }
    }

    public boolean saveBullets(Context c, List<Sprite> bullets) {
        return FileUtil.writeObject(c, fileName + "." + BULLETS, bullets);
    }

    public List<Sprite> loadBullets(Context c) {
        return loadSpriteList(c, fileName + "." + BULLETS);
    }

    public boolean saveObstacles(Context c, List<Sprite> obstacles) {
        return FileUtil.writeObject(c, fileName + "." + OBSTACLES, obstacles);
    }

    public List<Sprite> loadObstacles(Context c) {
        return loadSpriteList(c, fileName + "." + OBSTACLES);
    }

    public boolean saveCoins(Context c, List<Sprite> coins) {
        return FileUtil.writeObject(c, fileName + "." + COINS, coins);
    }

    public List<Sprite> loadCoins(Context c) {
        return loadSpriteList(c, fileName + "." + COINS);
    }

    // generic helper for loading lists of sprites
    private List<Sprite> loadSpriteList(Context c, String filePath) {
        File f = new File(filePath);
        if(f.exists()) {
            return (List<Sprite>) FileUtil.readObject(c, filePath);
        } else {
            return new LinkedList<>();
        }
    }
}
