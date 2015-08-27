import java.util.ArrayList;
import java.util.Random;

/**
 * Auto-generation of sprites
 */
public class Map {

    // available mapTiles. Element index is Tile ID
    private Sprite[] mapTiles;

    // grid of tile ID's instructing how to display map
    private byte[][] map;

    // number of tiles elapsed since last map was generated
    private int mapTileCounter;

    // keeps track of tile spaceship was on last time scroll() was called
    private int lastTile;

    // speed for "stationary" tiles to scroll across the map
    private float scrollSpeed;

    // starting scroll speed
    private final float startingScrollSpeed = -4.0f;

    // generated sprites
    private ArrayList<Sprite> tiles;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // difficulty level
    private float difficulty;

    // amount to increment difficulty by each frame
    private final float difficultyIncrement = 0.001f;

    // amount to incrmeent speed each frame
    private final float scrollSpeedIncrement = 0.0005f;

    // coordinates of upper-left of "window" being shown
    private int x;

    // dimensions of mapTiles
    private int tileWidth;
    private int tileHeight;

    private Random random;

    public ArrayList<Sprite> getTiles(){
        return tiles;
    }

    public float getScrollSpeed() { return scrollSpeed; }

    // construct mapTiles using names of tile files
    public Map(Sprite[] mapTiles) {
        // element zero must be left empty
        this.mapTiles = new Sprite[mapTiles.length + 1];
        System.arraycopy(mapTiles, 0, this.mapTiles, 1, this.mapTiles.length - 1);

        initMap();
    }

    private void initMap() {
        this.x = 0;
        tiles = new ArrayList<>();
        scrollSpeed = startingScrollSpeed;
        difficulty = 1.0f;

        tileWidth = this.mapTiles[1].getCurrentImage().getWidth(null);
        tileHeight = this.mapTiles[1].getCurrentImage().getHeight(null);

        random = new Random();

        map = new byte[][] {
                {0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0}
        };
        mapTileCounter = 0;
        lastTile = 0;
    }

    // current horizontal tile
    private int getWTile() { return x / tileWidth; }

    // adds any new tiles and generates a new set of tiles if needed
    public void update() {
        scrollSpeed += scrollSpeedIncrement;
        this.x += scrollSpeed;
        difficulty += difficultyIncrement;

        // perform rendering if spaceship has changed tiles
        if(getWTile() != lastTile) {
            for (int i = 0; i < map.length; i++) {
                // add any non-empty tiles in the current row at the edge of the screen
                if (map[i][mapTileCounter] != 0) {
                    addTile(getMapTile(map[i][mapTileCounter]), SCREEN_WIDTH, i * tileWidth);
                }
            }
            mapTileCounter++;

            // generate more tiles
            if (mapTileCounter == map[0].length) {
                map = generateTiles(6, 10);
                mapTileCounter = 0;
            }
            lastTile = getWTile();
        }
    }

    //
    private Sprite getMapTile(int index) {
        // todo: better way of setting speed. Shouldn't be a combined function
        Sprite tile = mapTiles[index];
        if(tile instanceof Obstacle) {
            tile = new Obstacle("obstacle_tile.png");
            tile.setSpeedX(scrollSpeed);
            return tile;
        }
        return null;
    }

    // adds sprite to arraylist and sets specified coordinates
    private void addTile(Sprite s, int x, int y) {
        s.setX(x);
        s.setY(y);
        tiles.add(s);
    }

    // difficulty determines:
    // type of obstacles (int(difficulty) determines range of tiles to use
    // frequency of obstacles
    // speed of oncoming obstacles
    // powerups and coins
    public byte[][] generateTiles(int rows, int col) {
        Random random = new Random();
        byte[][] tiles = new byte[rows][col];

        for(int i = 0; i < tiles[0].length; i++) {
            generateObstacle(i, tiles);
            i += random.nextInt((int) (7 / difficulty));
        }
        return tiles;
    }

    // generates single or cluster of simple obstacle at index in map
    // returns space to leave empty after index in map
    private void generateObstacle(int index, byte[][] map) {
        int row = random.nextInt(6);
        map[row][index] = 1;
        if(getP(0.6f) && map[0].length > index + 1) {
            map[row][index + 1] = 1;
            if(getP(0.4f) && map.length > row + 1) {
                map[row + 1][index] = 1;
            }
        }
    }

    // give probability of an event occurring
    // uses random numbers and will return if event should
    // occur or not
    private boolean getP(float probability) {
        return probability * 100 <= random.nextInt(100) + 1;
    }
}
