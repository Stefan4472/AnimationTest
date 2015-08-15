import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 8/14/2015.
 */
public class Background {

    // full background, which can be larger than screen
    private BufferedImage defaultImage;

    // dimensions of defaultImage
    private int width;
    private int height;

    // current portion of background being displayed
    private Image currentImage;

    // dimensions of screen display
    private final int SCREEN_WIDTH = 400;
    private final int SCREEN_HEIGHT = 300;

    // coordinates of upper-left of "window" being shown
    private int x;
    private int y;


    public Background(File defaultImage, int x, int y) throws IndexOutOfBoundsException {
        try {
            this.defaultImage = ImageIO.read(defaultImage);
        } catch(IOException e) {

        }
        width = this.defaultImage.getWidth();
        height = this.defaultImage.getHeight();
        currentImage = this.defaultImage.getSubimage(x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    // returns current part of background being displayed
    public Image getCurrentImage() {
        return currentImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // shifts window and updates currentImage
    // will shift as far as possible without going past edge of defaultImage
    // if window is at the edge of defaultImage, does nothing
    public Image scroll(int x, int y) {
        this.x += x;
        this.y += y;
        System.out.println("this.x = " + this.x + " and this.y = " + this.y);
        // keep scrolling in bounds
        if(this.x + SCREEN_WIDTH > width)
            this.x = width - SCREEN_WIDTH;
        else if(this.x < 0)
            this.x = 0;

        if(this.y + SCREEN_HEIGHT > height)
            this.y = height - SCREEN_HEIGHT;
        else if(this.y < 0)
            this.y = 0;
        
        currentImage = defaultImage.getSubimage(this.x, this.y, SCREEN_WIDTH, SCREEN_HEIGHT);
        return currentImage;
    }

    // sends window to (x,y)
    public Image goTo(int x, int y) {
        this.x = x;
        this.y = y;
        return scroll(0, 0);
    }
}
