import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

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
    protected Rectangle.Double hitBox;
    protected int hitBoxWidth;
    protected int hitBoxHeight;
    // offset of hitbox from sprite's point of drawing (top left)
    protected int hitBoxOffsetX;
    protected int hitBoxOffsetY;
    // lines describing movement of hitbox from frame to frame. Only
    // used if collides = true
    private Line2D movement1;
    private Line2D movement2;

    // sprite default image
    protected BufferedImage defaultImage;

    // what sprite actually looks like now (for animations)
    protected BufferedImage currentImage;

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
        moving = false;
        collision = false;
        collides = true;
        speedX = 0.0f;
        speedY = 0.0f;
    }

    // loads sprite's default image
    protected void loadDefaultImage(String imageName) {
        try {
            currentImage = defaultImage = ImageIO.read(new File(imageName));
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

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setCoordinates(int x, int y) { this.x = x; this.y = y; }

    public float getSpeedX() { return speedX; }
    public float getSpeedY() { return speedY; }

    public void setSpeedX(float speedX) { this.speedX = speedX; }
    public void setSpeedY(float speedY) { this.speedY = speedY; }

    public boolean isVisible() { return vis; }
    public void setVisible(Boolean visible) { vis = visible; }

    public boolean collides() { return collides; }
    public void setCollides(boolean collides) { this.collides = collides; }

    public boolean collision() { return collision; }
    public void setCollision(boolean collision) { this.collision = collision; }

    // calls methods to update sprites animations, actions,
    // and speed
    public void update() {
        updateCurrentImage();
        updateActions();
        updateSpeedX();
        updateSpeedY();
    }

    // updates sprite animations
    abstract void updateCurrentImage();

    // update/handle any actions sprite takes
    abstract void updateActions();

    abstract void updateSpeedX();
    abstract void updateSpeedY();

    // moves sprite using speedX and speedY, updates hitbox,
    // and checks if sprite is still visible
    protected void move() {
        x += speedX;
        y += speedY;
        if(x > BOARD_WIDTH || x < -width)
            vis = false;
        if(y > BOARD_HEIGHT || y < -height)
            vis = false;
        updateHitbox();
    }

    // updates hitbox to current position of sprite
    protected void updateHitbox() {
        hitBox.setRect(x + hitBoxOffsetX, y + hitBoxOffsetY, hitBoxWidth, hitBoxHeight);
    }

    // updates movement1 and movement2 Lines
    protected void updateMovements() { // todo: look into linesIntersect
        int start_x = x + hitBoxOffsetX;
        int start_y = y + hitBoxOffsetY;
        movement1.setLine(start_x, start_y, start_x + speedX, start_y + speedY);
        movement2.setLine(start_x, y + height - start_y, start_x + speedX, y + height - start_y + speedY);
    }

    // draws sprite at current coordinates on g
    public void render(Graphics2D g, ImageObserver o) {
        g.drawImage(currentImage, x, y, o);
    }

    // returns whether intended movement of sprites
    // will cause a collision
    // todo: calculate specific point of collides and use setX and setY methods to move sprites there
    public boolean collidesWith(Sprite s) { // todo: set flag first then setSpeed
        if(collides == false || s.collides == false)
            return false;
        //if(distanceTo(s) > 120)
        //    return false;
        //if(this instanceof Bullet || this instanceof  Rocket && s instanceof  Bullet || s instanceof  Rocket)
        //    return false;

        return hitBox.intersects(s.hitBox);
    }

    public double distanceTo(Sprite s) {
        return Math.sqrt((x - s.x)^2 + (y - s.y)^2);
    }
}
