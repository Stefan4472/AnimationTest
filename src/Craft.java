import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

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
    private Image movingImage;

    /**
     * Default constructor, initializes sprite.
     */
    public Craft() {
        initCraft();
    }

    private void initCraft() {
        defaultImage = new ImageIcon("spaceship.png").getImage();
        movingImage = new ImageIcon("spaceship_moving.png").getImage();

        currentImage = defaultImage;

        x = 40;
        y = 60;
    }

    /**
     * Changes coordinates of sprite by adding x- and y-
     * vectors to current position.
     */
    public void move() {
        x += dx;
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

        // set sprite to moving image
        currentImage = movingImage;

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

        // set sprite image back to default
        currentImage = defaultImage;
        
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
