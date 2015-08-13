import javax.swing.*;
import java.awt.*;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Created by Stefan on 8/12/2015.
 */
public class Sprite {

    // coordinates of sprite
    protected int x;
    protected int y;

    // sprite width and height
    protected int width;
    protected int height;

    // whether or not sprite is visible
    protected boolean vis;

    // sprite default image
    protected Image defaultImage;

    // what sprite actually looks like now (for animations)
    protected Image currentImage;

    // whether or not sprite is currently moving
    private boolean moving;

    // sets sprite coordinates
    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        vis = true;
    }

    // loads sprite's default image
    protected void loadDefaultImage(String imageName) {
        ImageIcon icon = new ImageIcon(imageName);
        defaultImage = icon.getImage();
    }

    // returns dimensions of sprite image
    protected void getImageDimensions() {
        width = defaultImage.getWidth(null);
        height = defaultImage.getHeight(null);
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return vis;
    }

    public void setVisible(Boolean visible) {
        vis = visible;
    }
}
