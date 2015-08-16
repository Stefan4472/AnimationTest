import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Spaceship extends Sprite {

    /**
     * Movement vector in x-direction.
     */
    private int dx;

    /**
     * Movement vector in y-direction.
     */
    private int dy;

    // Sprite's image when moving
    private SpriteAnimation movingAnimation;

    private SpriteAnimation startMovingAnimation;

    private float acceleration;

    // keeps track of fired rockets
    private ArrayList<Rocket> rockets;

    /**
     * Default constructor, initializes sprite.
     */
    public Spaceship(int x, int y) {
        super(x, y);
        initCraft();
    }

    private void initCraft() {
        defaultImage = new ImageIcon("spaceship.png").getImage();

        try {
            startMovingAnimation =
                    new SpriteAnimation("spaceship_starting_spritesheet.png", 50, 50, 1, false);
        } catch(IOException e) {}

        try {
            movingAnimation = new SpriteAnimation("spaceship_moving_spritesheet.png", 50, 50, 1, true);
        } catch(IOException e) {}

        currentImage = defaultImage;

        moving = false;
        acceleration = 0;

        getImageDimensions();

        rockets = new ArrayList<>();
    }

    public ArrayList<Rocket> getRockets() { return rockets; }

    //
    public void move() {
        // sprite direction not equal to zero
        if(dx != 0 || dy != 0) {
            // sprite was previously not moving. Play startmoving animation and reset acceleration
            if(moving == false) {
                currentImage = startMovingAnimation.start();
            } else { // sprite was previously moving. Increase acceleration
                if(startMovingAnimation.isPlaying()) {
                    currentImage = startMovingAnimation.nextFrame();
                } else { // Play moving animation as soon as startmoving animation is over
                    currentImage = movingAnimation.nextFrame(); // todo: is moving animation necessary? doesn't seem to make a noticeable difference
                }
            }
            moving = true;
        } else {
            currentImage = defaultImage;
            moving = false;
        }

        if(moving == true && acceleration < 0.1)
            acceleration += 0.01;
        else if(moving == true && acceleration < 2.0)
            acceleration += 0.05;
        else if(moving == false && acceleration >= 0.0)
            acceleration -= 0.05;

        x += dx + dx * acceleration; // todo: once user stops pressing arrow key, dx = 0, so total speed cuts to 0 instead of slowing down
        y += dy;
    }

    // fires rocket
    public void fire() {
        Rocket r1 = new Rocket(x + 43, y + 15);
        Rocket r2 = new Rocket(x + 43, y + 33);
        rockets.add(r1);
        rockets.add(r2);
    }

    public int getX() { return x; }

    public void setX(int x) { this.x = x; }

    // x-coordinate of center of sprite
    public int getCenterX() { return x + 31; }

    public int getY() { return y; }

    public void setY(int y) { this.y = y;}

    // y-coordinate of center of sprite
    public int getCenterY() { return y + 25; }

    /**
     * Returns this sprite's currentImage.
     * @return
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /**
     * Sets direction of sprite based on key pressed.
     * @param e
     */
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -1;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 1;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -1;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 1;
        }

        if(key == KeyEvent.VK_SPACE)
            fire();
    }

    /**
     * Sets movement direction to zero once key is released
     * @param e
     */
    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
