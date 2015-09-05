import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Auto-generation of sprites
 */
public class Map {

    // available mapTiles. Element index is Tile ID
    private Sprite[] mapTiles;

    // grid of tile ID's instructing how to display map
    private byte[][] map;

    // number of rows of tiles that fit in map
    private int rows;

    // number of tiles elapsed since last map was generated
    private int mapTileCounter;

    // keeps track of tile spaceship was on last time scroll() was called
    private long lastTile;

    // speed for "stationary" tiles to scroll across the map
    private float scrollSpeed;

    // generated sprites
    private ArrayList<Sprite> tiles;

    // board upon which Map lies
    private Board board;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private long x;

    // dimensions of mapTiles
    private int tileWidth;
    private int tileHeight;

    private Random random;

    public ArrayList<Sprite> getTiles(){ return tiles; }
    public float getScrollSpeed() { return scrollSpeed; }
    public void setBoard(Board board) { this.board = board; }

    public ArrayList<Sprite> getProjectiles() {
        return (ArrayList<Sprite>) tiles.stream()
                .filter(s -> s.getClass().equals(Alien.class))
                .map(s -> ((Alien) s).getProjectiles())
                .flatMap(b -> b.stream())
                .collect(Collectors.toList());
    }

    public Map(Sprite[] mapTiles) {
        // element zero must be left empty
        this.mapTiles = new Sprite[mapTiles.length + 1];
        System.arraycopy(mapTiles, 0, this.mapTiles, 1, this.mapTiles.length - 1);

        initMap();
    }

    private void initMap() {
        this.x = 0;
        tiles = new ArrayList<>();
        scrollSpeed = -4.0f;

        tileWidth = this.mapTiles[1].getWidth();
        tileHeight = this.mapTiles[1].getHeight();
        rows = SCREEN_HEIGHT / tileHeight;

        random = new Random();

        map = new byte[][] {
                {0, 0, 0, 0, 0, 0, 0},
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
    private long getWTile() { return x / tileWidth; }

    // number of pixels from start of current tile
    private int getWOffset() { return (int) x % tileWidth; }

    // adds any new tiles and generates a new set of tiles if needed
    public void update() {
        scrollSpeed -= 0.0005f;
        this.x += (int) Math.ceil(scrollSpeed);

        // perform rendering if spaceship has changed tiles
        if(getWTile() != lastTile) {
            for (int i = 0; i < map.length; i++) {
                // add any non-empty tiles in the current column at the edge of the screen
                if (map[i][mapTileCounter] != 0) {
                    addTile(getMapTile(map[i][mapTileCounter], SCREEN_WIDTH + getWOffset(),  i * tileWidth),
                          (int) Math.ceil(getScrollSpeed()), 0, board);
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
    private Sprite getMapTile(int index, double x, double y) {
        // todo: better way of setting speed. Shouldn't be a combined function
        Sprite tile = mapTiles[index];
        if(tile instanceof Obstacle) {
            tile = new Obstacle("obstacle_tile.png", x, y);
            if(index == 2)
                tile.setCollides(false);
        } else if(tile instanceof  Coin) {
            tile = new Coin("coin_tile.png", x, y);
        } else if(tile instanceof Alien) {
            tile = new Alien("alien.png", x, y);
        }
        return tile;
    }

    // sets specified fields and adds sprite to arraylist
    private void addTile(Sprite s, int speedX, int speedY, Board board) {
        s.setSpeedX(speedX);
        s.setSpeedY(speedY);
        s.setBoard(board);
        tiles.add(s);
    }

    // difficulty determines:
    // type of obstacles (int(difficulty) determines range of tiles to use
    // frequency of obstacles
    // speed of oncoming obstacles
    // powerups and coins
    public byte[][] generateTiles(int rows, int col) {
        Random random = new Random();
        if(getP(getPTile())) {
            if(getP(getPTunnel())) {
                map = generateTunnel();
            } else {
                map = generateObstacle();
            }
        } else {
            if(getP(0.5)) {
                if(getP(getPAlienSwarm())) {
                    map = generateAlienSwarm();
                } else {
                    map = generateAlien();
                }
            } else {
                map = generateAsteroid();
            }
        }
        return tiles;
    }

    // generates single or cluster of simple obstacle at index in map
    // returns space to leave empty after index in map // todo: row size shoudln't be hardcoded
    private byte[][] generateObstacle() {
        int size = 10 + random.nextInt(5);
        byte[][] generated = new byte[rows][size];
        for(int i = 0; i < size; i++) {
            if(getP(0.3f)) {
                int row = random.nextInt(rows);
                generated[row][i] = 1;
                if(getP(0.5) && i + 1 < size) {
                    generated[row][i + 1] = 1;
                }
                if(getP(0.3) && row + 1 < rows) {
                    generated[row + 1][i] = 1;
                }
                if(getP(0.2) && row > 0) {
                    generated[row - 1][i] = 1;
                }
            }
        }
        return generated;
    }

    // generates tunnel
    private byte[][] generateTunnel() {
        int size = 15 + random.nextInt(10);
        byte[][] generated = new byte[rows][size];
        int row = 1 + random.nextInt(4);
        float change_path = 0.0f;
        for(int i = 0; i < rows; i++) {
            if(i != row)
                map[i][0] = 1;
        }
        for(int i = 0; i < size; i++) {
            if(getP(change_path)) {
                change_path = -0.1f;
                int direction;
                if(getP(0.5f)) {
                    direction = 1;
                } else {
                    direction = -1;
                }
                if(direction == 1 && row == map.length - 1) {
                    direction = -1;
                } else if(direction == -1 && row == 0) {
                    direction = 1;
                }
                for(int j = 0; j < map.length; j++) {
                    if(j != row && j != row + direction) {
                        map[j][i] = 1;
                    }
                }
                row += direction;
            } else {
                for(int j = 0; j < map.length; j++) {
                    if(j < row - 1 ||j > row + 1) {
                        map[j][i] = 2;
                    } else if(j != row) {
                        map[j][i] = 1;
                    } else {
                        map[j][i] = 3;
                    }
                }
            }
            change_path += 0.05f;
        }
        return generated;
    }

    private void generateAlien(int index, byte[][] map) {
        map[random.nextInt(6)][index] = 4;
    }

    // give probability of an event occurring
    // uses random numbers and will return if event should
    // occur or not
    private boolean getP(double probability) {
        return random.nextInt(100) + 1 <= probability * 100;
    }

    // calculates and returns probability of a tile-based obstacle
    private double getPTile() {
        if (110 - board.getDifficulty() >= 50) {
            return (110 - board.getDifficulty()) / 100;
        } else {
            return 0.5;
        }
    }

    // calculates and returns probability of aliens appearing in a swarm
    private double getPAlienSwarm() {
        return (-30 + board.getDifficulty()) / 100;
    }
}
