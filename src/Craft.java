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
     * This sprite's image/
     */
    private Image image;

    /**
     * Default constructor, initializes sprite.
     */
    public Craft() {
        initCraft();
    }

    private void initCraft() {
        ImageIcon icon = new ImageIcon("spaceship.png");
        image = icon.getImage();
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
     * Returns this sprite's image.
     * @return
     */
    public Image getImage() {
        return image;
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
