package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by Stefan on 8/12/2015.
 */
public abstract class Sprite {

    // coordinates of sprite
    protected double x;
    protected double y;

    // intended movement in x and y directions each frame
    protected double speedX;
    protected double speedY;

    // sprite width and height
    protected int width;
    protected int height;

    // damage sprite can withstand and damage it inflicts on collision
    protected int hp;
    protected int damage;

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
    protected Bitmap defaultImage;

    // board on which this sprite exists
    protected Board board;

    // random number generator
    protected Random random;

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public double getSpeedX() {
        return speedX;
    }
    public double getSpeedY() {
        return speedY;
    }
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }
    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
    public boolean isVisible() {
        return vis;
    }
    public void setVisible(Boolean visible) {
        vis = visible;
    }
    public boolean getCollides() {
        return collides;
    }
    public void setCollides(boolean collides) {
        this.collides = collides;
    }
    public boolean getCollision() {
        return collision;
    }
    public void setCollision(boolean collision) {
        this.collision = collision;
    }
    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getHP() {
        return hp;
    }
    public void setHP(int hp) {
        this.hp = hp;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Sprite(double x, double y, Bitmap defaultImage, Board board) {
        this.x = x;
        this.y = y;
        this.defaultImage = defaultImage;
        this.board = board;
        initSprite();
    }

    // initializes with sprite at (0,0)
    public Sprite(Bitmap defaultImage, Board board) {
        this(0, 0, defaultImage, board);
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
        damage = 0;
        hp = 0;
    }

    // updates fields with dimensions of sprite image
    protected void getImageDimensions() {
        width = defaultImage.getWidth();
        height = defaultImage.getHeight();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
        hitBox.updateCoordinates((int) x, hitBox.getY());
    }

    public void setY(double y) {
        this.y = y;
        hitBox.updateCoordinates(hitBox.getX(), (int) y);
    }

    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
        hitBox.updateCoordinates((int) x, (int) y);
    }

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
        if (x > BOARD_WIDTH + width || x < -width)
            vis = false;
        if (y > BOARD_HEIGHT || y < -height) // todo: bounce of edge of screen
            vis = false;
        hitBox.updateCoordinates((int) x, (int) y);
    }

    // draws sprite at current coordinates on g
    abstract void draw(Canvas canvas);

    // returns whether intended movement of sprites
    // will cause a collision
    // todo: calculate specific point of collides and use setX and setY methods to move sprites there
    public boolean collidesWith(Sprite s) { // todo: set flag first then setSpeed
        if (!collides || !s.collides)
            return false;
        return hitBox.intersects(s.hitBox);
    }

    // returns distance between origin points of sprites
    public double distanceTo(Sprite s) {
        return Math.sqrt(Math.pow((s.x - x), 2) + Math.pow((s.y - y), 2));
    }

    // returns coordinates of center of sprite's hitbox
    // as a Point2D object
    public Point2D getHitboxCenter() {
        return new Point2D(hitBox.getX() + hitBox.getWidth() / 2,
                hitBox.getY() + hitBox.getHeight() / 2);
    }

    public boolean getP(double probability) {
        return random.nextInt(1000000) + 1 <= probability * 1000000;
    }
}
