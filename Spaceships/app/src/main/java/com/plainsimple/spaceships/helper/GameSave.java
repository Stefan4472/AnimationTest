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
    private static final String BACKGROUND = "BACKGROUNDFILE";
    private static final String SCOREDISPLAY = "SCOREDISPLAYFILE";
    private static final String HEALTHBAR = "HEALTHBARFILE";

    public boolean saveMap(Map map) { // todo: custom saving and restoring of the objects
        return FileUtil.writeObject(new File(directory, MAP), map);
    }

    // loads saved data into the given Map object
    public void loadMap(Map restore) {
        /*Map saved = (Map) FileUtil.readObject(new File(directory, MAP));
        restore.setTiles(saved.getTiles());
        restore.setTileGenerator(saved.getTileGenerator());
        restore.setMapTileCounter(saved.getMapTileCounter());
        restore.setLastTile(saved.getLastTile());
        restore.setX(saved.getX());
        restore.setObstacles(saved.getObstacles());
        restore.setObstaclesInvis(saved.getObstaclesInvis());
        restore.setCoins(saved.getCoins());
        restore.setAliens(saved.getAliens());
        restore.setAlienProjectiles(saved.getAlienProjectiles());*/
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
}
