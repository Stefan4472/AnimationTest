import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Craft {

    /**
     * Movement vector in x-direction.
     */
    private int dx;

    /**
     * Movement vector in y-direction.
     */
    private int dy;

    /**
     * Current position horizontally in coordinate plane.
     */
    private int x;

    /**
     * Current postion vertically in coordinate plane
     */
    private int y;

    /**
     * The sprite's current currentImage/
     */
    private Image currentImage;

    // Sprite's default image
    private Image defaultImage;

    // Sprite's image when moving
    private SpriteAnimation movingAnimation;

    private SpriteAnimation startMovingAnimation;

    // whether sprite is moving or not
    private boolean moving;

    private int acceleration;

    /**
     * Default constructor, initializes sprite.
     */
    public Craft() {
        initCraft();
    }

    private void initCraft() {
        defaultImage = new ImageIcon("spaceship.png").getImage();

        startMovingAnimation = new SpriteAnimation(new File[] {
                new File("spaceship_starting1.png"),
                new File("spaceship_starting2.png"),
                new File("spaceship_starting3.png"),
                new File("spaceship_starting4.png"),
                new File("spaceship_starting5.png")
        }, false);

        movingAnimation = new SpriteAnimation(new File[] {
                new File("spaceship_moving1.png"),
                new File("spaceship_moving2.png")
        }, true);

        currentImage = defaultImage;

        moving = false;
        acceleration = 0;

        x = 40;
        y = 60;
    }

    /**
     * Changes coordinates of sprite by adding x- and y-
     * vectors to current position.
     */
    public void move() {
        // sprite direction not equal to zero
        if(dx != 0 || dy != 0) {
            // sprite was previously not moving. Play startmoving animation and reset acceleration
            if(moving == false) {
                currentImage = startMovingAnimation.start();
            } else { // sprite was previously moving. Increase acceleration
                if(startMovingAnimation.isPlaying()) {
                    currentImage = startMovingAnimation.nextFrame();
                    acceleration++;
                } else { // Play moving animation as soon as startmoving animation is over
                    currentImage = movingAnimation.nextFrame();
                }

            }
            //currentImage = movingImage;
            moving = true;
        } else {
            currentImage = defaultImage;
            moving = false;
            acceleration = 0;
        }

        x += dx + dx * acceleration;
        y += dy;
    }

    /**
     * Returns x-coordinate of sprite's current position.
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Returns y-coordinate of sprite's current position.
     * @return
     */
    public int getY() {
        return y;
    }

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
