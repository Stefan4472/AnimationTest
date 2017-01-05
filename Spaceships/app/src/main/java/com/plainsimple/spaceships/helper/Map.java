package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.Canvas;

import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Alien1;
import com.plainsimple.spaceships.sprite.Coin;
import com.plainsimple.spaceships.sprite.Obstacle;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.GameEngineUtil;

import java.util.LinkedList;
import java.util.List;

import static com.plainsimple.spaceships.view.GameView.screenW;

/**
 * Generates sprites, among other things: basically the game driver
 */

public class Map {

    private Context context;
    // grid of tile ID's instructing which sprites to initialize on screen
    private byte[][] map;
    // used to generate tile-based terrain
    private TileGenerator tileGenerator;
    // number of rows of sprites that fit on screen
    private static final int ROWS = 6;
    // number of tiles elapsed since last map was generated
    private int mapTileCounter = 0;
    // keeps track of tile spaceship was on last time map was updated
    private long lastTile = 0;
    // coordinates of upper-left of "window" being shown
    private long x = 0;

    private float difficulty;
    private int screenW;
    private int screenH;

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

    public Map(Context context, int screenW, int screenH) {
        this.context = context;
        this.screenW = screenW;
        this.screenH = screenH;
        tileWidth = screenH / ROWS;
        map = new byte[ROWS][screenW / tileWidth];
        tileGenerator = new TileGenerator(ROWS);
    }

    // creates new sprites as specified by the map
    // generates new map chunks if needed
    public void update(float difficulty, float scrollSpeed, Spaceship spaceship) {
        this.difficulty = difficulty;

        // update x
        x += screenW * scrollSpeed;

        // check if screen has progressed to render a new tile
        if (getWTile() != lastTile) {
            Sprite to_generate;
            // add any non-empty tiles in the current column to the edge of the screen
            for (int i = 0; i < map.length; i++) {
                if (map[i][mapTileCounter] != TileGenerator.EMPTY) {
                    to_generate = getMapTile(map[i][mapTileCounter], screenW + getWOffset(), i * tileWidth, spaceship);
                    addTile(to_generate, scrollSpeed, 0); // todo: put speedX and speedY in constructor? -> Make scrollSpeed static and have sprites determine speedX and speedY on initialization?
                }
            }
            mapTileCounter++;

            // generate more sprites
            if (mapTileCounter == map[0].length) {
                //map = tileGenerator.generateTiles(GameActivity.getDifficulty());
                map = tileGenerator.generateDebugTiles();
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }

        GameEngineUtil.getAlienBullets(alienProjectiles, aliens);
        // check collisions between user-fired projectiles and relevant sprites
        for(Sprite projectile : spaceship.getProjectiles()) {
            GameEngineUtil.checkCollisions(projectile, aliens);
            GameEngineUtil.checkCollisions(projectile, obstacles);
        }
        GameEngineUtil.checkCollisions(spaceship, aliens);
        GameEngineUtil.checkCollisions(spaceship, obstacles);
        GameEngineUtil.checkCollisions(spaceship, coins);
        GameEngineUtil.checkCollisions(spaceship, alienProjectiles);
        GameEngineUtil.updateSprites(obstacles);
        GameEngineUtil.updateSprites(aliens);
        GameEngineUtil.updateSprites(coins);
        GameEngineUtil.updateSprites(alienProjectiles);
    }

    // current horizontal tile
    private long getWTile() {
        return x / tileWidth;
    }

    // number of pixels from start of current tile
    private int getWOffset() {
        return (int) x % tileWidth;
    }

    // returns sprite initialized to coordinates (x,y) given tileID
    private Sprite getMapTile(int tileID, int x, int y, Spaceship spaceship) throws IndexOutOfBoundsException {
        switch (tileID) {
            case TileGenerator.OBSTACLE:
                return new Obstacle(BitmapCache.getData(BitmapID.OBSTACLE, context), x, y);
            case TileGenerator.OBSTACLE_INVIS:
                Sprite tile = new Obstacle(BitmapCache.getData(BitmapID.OBSTACLE, context), x, y);
                tile.setCollides(false);
                return tile;
            case TileGenerator.COIN:
                return new Coin(BitmapCache.getData(BitmapID.COIN, context), AnimCache.get(BitmapID.COIN_SPIN, context), x, y);
            case TileGenerator.ALIEN_LVL1:
                Alien1 alien_1 = new Alien1(BitmapCache.getData(BitmapID.ALIEN, context), x, y, spaceship);
                alien_1.injectResources(BitmapCache.getData(BitmapID.ALIEN_BULLET, context), AnimCache.get(BitmapID.SPACESHIP_EXPLODE, context));
                return alien_1;
            default:
                throw new IndexOutOfBoundsException("Invalid tileID (" + tileID + ")");
        }
    }

    // sets specified fields and adds sprite to arraylist
    private void addTile(Sprite s, float speedX, float speedY) {
        s.setSpeedX(speedX);
        s.setSpeedY(speedY);
        if (s instanceof Obstacle) {
            obstacles.add(s);
        } else if (s instanceof Alien) {
            aliens.add(s);
        } else if (s instanceof Coin) {
            coins.add(s);
        }
    }

    public void draw(Canvas canvas, Context context) {
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
}
