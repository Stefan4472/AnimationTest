package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Asteroid;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.GameEngineUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Generates and updates all sprites other than the spaceship. Also does collision detection.
 */

public class GameDriver {

    private Context context;
    // grid of tile ID's instructing which sprites to initialize on screen
    private byte[][] tiles;
    // used to generate tiles based on pre-defined settings
    private Map map;
    // number of rows of sprites that fit on screen
    private static final int ROWS = 6;
    // number of tiles elapsed since last tiles was generated
    private int mapTileCounter = 0;
    // keeps track of tile spaceship was on last time tiles was updated
    private long lastTile = 0;
    // coordinates of upper-left of "window" being shown
    private float x = 0;

    private int difficulty;
    private int screenW;
    private int screenH;

    // the spaceship
    private Spaceship spaceship;
    // active generated obstacles
    private List<Sprite> obstacles = new LinkedList<>();
    // active generated coins
    private List<Sprite> coins = new LinkedList<>();
    // active generated aliens
    private List<Sprite> aliens = new LinkedList<>();
    // active projectiles on screen fired by aliens
    private List<Sprite> alienProjectiles = new LinkedList<>();

    // width (px) of the side of a tile
    private int tileWidth;

    // creates GameDriver using a copy of context, the screen dimensions, and a String defining the
    // map to be used.
    public GameDriver(Context context, int screenW, int screenH, String mapString) {
        this.context = context;
        this.screenW = screenW;
        this.screenH = screenH;
        tileWidth = screenH / ROWS;
        tiles = new byte[ROWS][screenW / tileWidth];
        // todo: take String defining map as a parameter
        map = Map.parse(mapString);
    }

    // creates new sprites as specified by the tiles
    // generates new tiles chunks if needed
    public void update(int difficulty, double scrollSpeed, Spaceship spaceship) {
        this.difficulty = difficulty;

        // update x
        x += screenW * scrollSpeed;

        // check if screen has progressed to render a new tile
        if (getWTile() != lastTile) {
            // add any non-empty tiles in the current column to the edge of the screen
            for (int i = 0; i < tiles.length; i++) {
                // process for adding Obstacles: count the number of adjacent obstacles in the row.
                // Set them all to EMPTY in the array. Construct the obstacle using this data
                if (tiles[i][mapTileCounter] == TileGenerator.OBSTACLE) {
                    int num_cols = 0;
                    for (int col = mapTileCounter; col < tiles[0].length && tiles[i][col] == TileGenerator.OBSTACLE; col++) {
                        num_cols++;
                        tiles[i][col] = TileGenerator.EMPTY;
                    }
                    obstacles.add(new Obstacle(screenW + getWOffset(), i * tileWidth, num_cols * tileWidth, tileWidth));

                } else if (tiles[i][mapTileCounter] != TileGenerator.EMPTY) {
                    addMapTile(tiles[i][mapTileCounter], screenW + getWOffset(), i * tileWidth, (float) scrollSpeed, spaceship);
                }
            }
            mapTileCounter++;

            // generate more sprites todo: only generate if all aliens have been killed, or all bosses, or etc. preventGeneration flag?
            if (mapTileCounter == tiles[0].length) {
                tiles = map.genNext(difficulty);
//                tiles = map.generateDebugTiles();
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }

        // todo: improve
        GameEngineUtil.getAlienBullets(alienProjectiles, aliens);

        // check collisions between user-fired projectiles and relevant sprites
        for(Sprite projectile : spaceship.getProjectiles()) {
            GameEngineUtil.checkCollisions(projectile, aliens);
            GameEngineUtil.checkCollisions(projectile, obstacles);
            //GameEngineUtil.checkCollisions(projectile, alienProjectiles);
        }
        // check collisions with spaceship only if terminate = false
        if (!spaceship.terminate()) {
            GameEngineUtil.checkCollisions(spaceship, aliens);
            GameEngineUtil.checkCollisions(spaceship, obstacles);
            GameEngineUtil.checkCollisions(spaceship, coins);
            GameEngineUtil.checkCollisions(spaceship, alienProjectiles);
        }
        // update all other sprites
        GameEngineUtil.updateSprites(obstacles);
        GameEngineUtil.updateSprites(aliens);
        GameEngineUtil.updateSprites(coins);
        GameEngineUtil.updateSprites(alienProjectiles);
    }

    // current horizontal tile
    private long getWTile() {
        return (int) x / tileWidth;
    }

    // number of pixels from start of current tile
    private int getWOffset() {
        return (int) x % tileWidth;
    }

    // initializes sprite and adds to the proper list, given parameters
    private void addMapTile(int tileID, float x, float y, float scrollSpeed, Spaceship spaceship) throws IndexOutOfBoundsException {
        switch (tileID) {
            case TileGenerator.COIN:
                coins.add(new Coin(x, y, context));
                break;
            case TileGenerator.ALIEN:
                aliens.add(new Alien(x, y,scrollSpeed, spaceship, difficulty, context));
                break;
            case TileGenerator.ASTEROID: // todo: separate list for asteroids? could bounce off one another
                obstacles.add(new Asteroid(x, y, scrollSpeed, difficulty, context));
                break;
            case TileGenerator.END_GAME:

                break;
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
    }

    public void draw(Canvas canvas, Context context) { // todo: causes concurrentmodificationexception on game restart. Use iterators?
        for (Sprite o : obstacles) {
            GameEngineUtil.drawSprite(o, canvas, context);
        }
        for (Sprite c : coins) {
            GameEngineUtil.drawSprite(c, canvas, context);
        }
        for (Sprite a : aliens) {
            GameEngineUtil.drawSprite(a, canvas, context);
        }
        for (Sprite a : alienProjectiles) {
            GameEngineUtil.drawSprite(a, canvas, context);
        }
    }

    // resets the map
    public void reset() {
        obstacles.clear();
        coins.clear();
        aliens.clear();
        alienProjectiles.clear();
        tiles = new byte[ROWS][screenW / tileWidth];
        map.restart();
        mapTileCounter = 0;
        x = 0;
        lastTile = 0;
        difficulty = 0;
    }
}
