import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
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
    protected boolean collision;
    // hitbox for collision detection todo: complex shapes
    protected Rectangle.Double hitBox;
    // offset of hitbox from sprite's point of drawing (top left)
    protected int hitBoxOffsetX;
    protected int hitBoxOffsetY;
    // lines describing movement of hitbox from frame to frame. Only
    // used if collision = true
    protected Line2D movement1;
    protected Line2D movement2;

    // sprite default image
    protected BufferedImage defaultImage;

    // what sprite actually looks like now (for animations)
    protected BufferedImage currentImage;

    // whether or not sprite is currently moving
    protected boolean moving; // todo: remove?

    // sets sprite coordinates
    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        vis = true;
        moving = false;
        speedX = 0.0f;
        speedY = 0.0f;
    }

    // initializes with image at (0,0)
    public Sprite(String imageName) {
        this(0, 0);
        loadDefaultImage(imageName);
    }

    // loads sprite's default image
    protected void loadDefaultImage(String imageName) {
        try {
            defaultImage = ImageIO.read(new File(imageName));
        } catch(IOException e) {
            e.printStackTrace();
        }
        currentImage = defaultImage;
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

    public boolean isVisible() { return vis; }
    public void setVisible(Boolean visible) { vis = visible; }

    // draws sprite at current coordinates on g
    public void render(Graphics2D g, ImageObserver o) {
        g.drawImage(currentImage, x, y, o);
    }

    //public void update();
    // public void move

    // updates movement1 and movement2 Lines
    protected void updateMovements() {
        int start_x = x + hitBoxOffsetX;
        int start_y = y + hitBoxOffsetY;
        movement1.setLine(start_x, start_y, start_x + speedX, start_y + speedY);
        movement2.setLine(start_x, y + height - start_y, start_x + speedX, y + height - start_y + speedY);
    }

    // returns whether intended movement of sprites
    // will cause a collision
    // use updateMovements to keep hitboxes up to date
    // todo: calculate specific point of collision and use setX and setY methods to move sprites there
    public boolean collidesWith(Sprite s) {
        if(collision == false || s.collision == false)
            return false;
        if(movement1.intersectsLine(s.movement1))
            return true;
        else if(movement1.intersectsLine(s.movement2))
            return  true;
        else if(movement2.intersectsLine(s.movement1))
            return true;
        else if(movement2.intersectsLine(s.movement2))
            return true;
        else
            return false;
    }
}
