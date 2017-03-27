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
 * Generates sprites, among other things: basically the game driver
 */

public class Map {

    private Context context;
    // grid of tile ID's instructing which sprites to initialize on screen
    private byte[][] tiles;
    // used to generate tile-based terrain
    private TileGenerator tileGenerator;
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

    // active generated obstacles
    private List<Sprite> obstacles = new LinkedList<>();
    // active generated non-colliding obstacles
    private List<Sprite> obstaclesInvis = new LinkedList<>();
    // active generated coins
    private List<Sprite> coins = new LinkedList<>();
    // active generated aliens
    private List<Sprite> aliens = new LinkedList<>(); // todo: any way to keep them in their subclasses?
    // active projectiles on screen fired by aliens
    private List<Sprite> alienProjectiles = new LinkedList<>();

    // width (px) of the side of a tile
    private int tileWidth;

    public Map(Context context, int screenW, int screenH) {
        this.context = context;
        this.screenW = screenW;
        this.screenH = screenH;
        tileWidth = screenH / ROWS;
        tiles = new byte[ROWS][screenW / tileWidth];
        tileGenerator = new TileGenerator(ROWS);
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
                if (tiles[i][mapTileCounter] != TileGenerator.EMPTY) {
                    addMapTile(tiles[i][mapTileCounter], screenW + getWOffset(), i * tileWidth, (float) scrollSpeed, spaceship);
                }
            }
            mapTileCounter++;

            // generate more sprites todo: only generate if all aliens have been killed, or all bosses, or etc. preventGeneration flag?
            if (mapTileCounter == tiles[0].length) {
//                tiles = tileGenerator.generateTiles(difficulty);
                tiles = tileGenerator.generateDebugTiles();
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }

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
        GameEngineUtil.updateSprites(obstaclesInvis);
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
            case TileGenerator.OBSTACLE:
                obstacles.add(new Obstacle(x, y, scrollSpeed, true, context));
                break;
            case TileGenerator.OBSTACLE_INVIS:
                obstaclesInvis.add(new Obstacle(x, y, scrollSpeed, false, context));
                break;
            case TileGenerator.COIN:
                coins.add(new Coin(x, y, scrollSpeed, context));
                break;
            case TileGenerator.ALIEN:
                aliens.add(new Alien(x, y,scrollSpeed, spaceship, difficulty, context));
                break;
            case TileGenerator.ASTEROID: // todo: separate list for asteroids? could bounce off one another
                obstacles.add(new Asteroid(x, y, scrollSpeed, difficulty, context));
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
    }

    public void draw(Canvas canvas, Context context) {
        for (Sprite o : obstacles) {
            GameEngineUtil.drawSprite(o, canvas, context);
        }
        for (Sprite o : obstaclesInvis) { // todo: causes concurrentmodificationexception on game restart. Use iterators?
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
        obstaclesInvis.clear();
        coins.clear();
        aliens.clear();
        alienProjectiles.clear();
        tiles = new byte[ROWS][screenW / tileWidth];
        tileGenerator = new TileGenerator(ROWS);
        mapTileCounter = 0;
        x = 0;
        lastTile = 0;
        difficulty = 0;
    }
}
