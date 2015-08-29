import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Stefan on 8/12/2015.
 */
public abstract class Sprite {

    // coordinates of sprite
    protected int x;
    protected int y;

    // intended movement in x and y directions each frame
    protected float speedX;
    protected float speedY;

    // sprite width and height
    protected int width;
    protected int height;

    // whether or not sprite is visible on screen
    protected boolean vis;
    // whether or not sprite can collide with other sprites
    protected boolean collides; // todo: flags all in one bitwise operator?
    // whether or not sprite has had a collision
    protected boolean collision;
    // whether or not sprite is currently moving
    protected boolean moving; // todo: remove?

    protected final int BOARD_WIDTH = 600;
    protected final int BOARD_HEIGHT = 400;

    // hitbox for collision detection todo: complex shapes
    protected Hitbox hitBox;

    // sprite default image
    protected BufferedImage defaultImage;

    // what sprite actually looks like now (for animations)
    protected BufferedImage currentImage;

    // board on which this sprite exists
    protected Board board;

    // random number generator
    private Random random;

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public float getSpeedX() { return speedX; }
    public float getSpeedY() { return speedY; }

    public void setSpeedX(float speedX) { this.speedX = speedX; }
    public void setSpeedY(float speedY) { this.speedY = speedY; }

    public boolean isVisible() { return vis; }
    public void setVisible(Boolean visible) { vis = visible; }

    public boolean getCollides() { return collides; }
    public void setCollides(boolean collides) { this.collides = collides; }

    public boolean getCollision() { return collision; }
    public void setCollision(boolean collision) { this.collision = collision; }

    public void setBoard(Board board) { this.board = board; }

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        initSprite();
    }

    // initializes with sprite at (0,0)
    public Sprite(String imageName) {
        this(imageName, 0, 0);
    }

    // sets sprite coordinates
    public Sprite(String imageName, int x, int y) {
        loadDefaultImage(imageName);
        this.x = x;
        this.y = y;
        initSprite();
    }

    private void initSprite() {
        vis = true;
        moving = true;
        collision = false;
        collides = true;
        speedX = 0.0f;
        speedY = 0.0f;
        hitBox = new Hitbox();
        random = new Random();
    }

    // loads sprite's default image
    protected void loadDefaultImage(String imageName) {
        try {
            currentImage = defaultImage = ImageIO.read(new File(imageName));
            getImageDimensions();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // updates fields with dimensions of sprite image
    protected void getImageDimensions() {
        width = defaultImage.getWidth(null);
        height = defaultImage.getHeight(null);
    }

    public Image getCurrentImage() { return currentImage; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) {
        this.x = x;
        hitBox.updateCoordinates(x, (int) hitBox.getY());
    }

    public void setY(int y) {
        this.y = y;
        hitBox.updateCoordinates((int) hitBox.getX(), y);
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
        hitBox.updateCoordinates(x, y);
    }

    // updates sprite animations
    abstract void updateCurrentImage();

    // update/handle any actions sprite takes
    abstract void updateActions();

    abstract void updateSpeeds();

    // handles collision with s
    abstract void handleCollision(Sprite s);

    // moves sprite using speedX and speedY, updates hitbox,
    // and checks if sprite is still visible
    protected void move() {
        x += speedX;
        y += speedY;
        // keep in mind sprites are generated past the screen
        if(x > BOARD_WIDTH + width || x < -width)
            vis = false;
        if(y > BOARD_HEIGHT || y < -height) // todo: bounce of edge of screen
            vis = false;
        hitBox.updateCoordinates(x, y);
    }

    // draws sprite at current coordinates on g
    public void render(Graphics2D g, ImageObserver o) {
        g.drawImage(currentImage, x, y, o);
    }

    // returns whether intended movement of sprites
    // will cause a collision
    // todo: calculate specific point of collides and use setX and setY methods to move sprites there
    public boolean collidesWith(Sprite s) { // todo: set flag first then setSpeed
        if(!collides || !s.collides)
            return false;
        return hitBox.intersects(s.hitBox);
    }

    // returns distance between origin points of sprites
    public float distanceTo(Sprite s) {
        return (float) Math.sqrt((x - s.x)^2 + (y - s.y)^2);
    }

    public boolean getP(float probability) {
        return random.nextInt(100) + 1 <= probability * 100;
    }
}
