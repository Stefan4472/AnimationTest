package com.plainsimple.spaceships.helper;

import android.content.Context;

import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.FileUtil;
import com.plainsimple.spaceships.view.HealthBar;
import com.plainsimple.spaceships.view.ScoreDisplay;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Saves game state to file and can re-create game state from file
 */
public class GameSave {

    // default save name
    public static final String DEFAULT_SAVE_NAME = "DefaultSave";

    // parent directory of all files in this save
    // named according to given saveName
    private File directory;

    // stores game state information (volume, currently equipped, etc)
    private static final String GAME_STATE_FILE = "GameStateFile";
    // stores serialized aliens
    private static final String ALIENS = "AliensFile";
    // stores serialized alien bullets
    private static final String ALIEN_BULLETS = "AlienBulletsFile";
    // stores serialized spaceship
    //private static final String SPACESHIP = "SpaceshipFile";
    // stores serialized spaceship bullets and rockets
    private static final String BULLETS = "BulletsFile";
    // stores serialized coins
    private static final String COINS = "CoinsFile";
    // stores serialized obstacles
    private static final String OBSTACLES = "ObstaclesFile";
    // stores background
    //private static final String BACKGROUND = "BackgroundFile";
    // stores tile generator

    public GameSave(Context context) {
        this(context, DEFAULT_SAVE_NAME);
    }

    public GameSave(Context context, String saveName) {
        directory = new File(context.getFilesDir(), saveName);
        directory.mkdirs();
    }

    // returns whether this save exists in storage on the device
    public static boolean exists(Context context, String saveName) { // todo: testing
        return (new File(context.getFilesDir(), saveName)).exists();
    }

    // deletes all save files associated with this save
    public void delete() {
        for (File f : directory.listFiles()) {
            f.delete();
        }
        directory.delete();
    }

    private static final String MAP = "MAPFILE";
    private static final String SPACESHIP = "SPACESHIPFILE";
    private static final String BACKGROUND = "BACKGROUNDFILE"; // todo
    private static final String SCOREDISPLAY = "SCOREDISPLAYFILE";
    private static final String HEALTHBAR = "HEALTHBARFILE";

    public boolean saveMap(Map map) { // todo: custom saving and restoring of the objects
        return FileUtil.writeObject(new File(directory, MAP), map);
    }

    public Map loadMap() {
        return (Map) FileUtil.readObject(new File(directory, MAP));
    }

    public boolean saveSpaceship(Spaceship spaceship) {
        return FileUtil.writeObject(new File(directory, SPACESHIP), spaceship);
    }

    public Spaceship loadSpaceship() {
        return (Spaceship) FileUtil.readObject(new File(directory, SPACESHIP));
    }

    public boolean saveBackground(Background backgroud) {
        return FileUtil.writeObject(new File(directory, BACKGROUND), backgroud);
    }

    public Background loadBackground() {
        return (Background) FileUtil.readObject(new File(directory, BACKGROUND));
    }

    public boolean saveScoreDisplay(ScoreDisplay scoreDisplay) {
        return FileUtil.writeObject(new File(directory, SCOREDISPLAY), scoreDisplay);
    }

    public ScoreDisplay loadScoreDisplay() {
        return (ScoreDisplay) FileUtil.readObject(new File(directory, SCOREDISPLAY));
    }

    public boolean saveHealthBar(HealthBar healthBar) {
        return FileUtil.writeObject(new File(directory, HEALTHBAR), healthBar);
    }

    public HealthBar loadHealthBar() {
        return (HealthBar) FileUtil.readObject(new File(directory, HEALTHBAR));
    }

    /*
    // writes currently stored values to file
    public void saveGameState(Context c) {

    }

    // creates a game state from the read file
    public void load() {

    }

    public boolean saveAliens(List<Sprite> aliens) {
        return FileUtil.writeObject(new File(directory, ALIENS), aliens);
    }

    public List<Sprite> loadAliens() {
        return loadSpriteList(new File(directory, ALIENS));
    }

    public boolean saveAlienBullets(List<Sprite> alienBullets) {
        return FileUtil.writeObject(new File(directory, ALIEN_BULLETS), alienBullets);
    }

    public List<Sprite> loadAlienBullets() {
        return loadSpriteList(new File(directory, ALIEN_BULLETS));
    }

    public boolean saveSpaceship(Spaceship spaceship) {
        return FileUtil.writeObject(new File(directory, SPACESHIP), spaceship);
    }

    public Spaceship loadSpaceship() {
        File f = new File(directory, SPACESHIP);
        return f.exists() ? (Spaceship) FileUtil.readObject(f) : null;
    }

    public boolean saveBullets(List<Sprite> bullets) {
        return FileUtil.writeObject(new File(directory, BULLETS), bullets);
    }

    public List<Sprite> loadBullets() {
        return loadSpriteList(new File(directory, BULLETS));
    }

    public boolean saveObstacles(List<Sprite> obstacles) {
        return FileUtil.writeObject(new File(directory, OBSTACLES), obstacles);
    }

    public List<Sprite> loadObstacles() {
        return loadSpriteList(new File(directory, OBSTACLES));
    }

    public boolean saveCoins(List<Sprite> coins) {
        return FileUtil.writeObject(new File(directory, COINS), coins);
    }

    public List<Sprite> loadCoins() {
        return loadSpriteList(new File(directory, COINS));
    }

    // generic helper for loading lists of sprites
    private List<Sprite> loadSpriteList(File file) {
        if(file.exists()) {
            return (List<Sprite>) FileUtil.readObject(file);
        } else {
            return new LinkedList<>();
        }
    }*/
}
