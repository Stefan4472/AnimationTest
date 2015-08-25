import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by Stefan on 8/17/2015.
 */
public class ImageUtil  {

    // draws as much as overlay as will fit on top of b, starting from top left
    // transparent pixels in the overlay are ignored
    public static BufferedImage layer(BufferedImage b, BufferedImage overlay) { // todo: optimizations (WritableRaster?)
        int height, width; // todo: layer overlay onto Graphics2D and return Graphics2D?

        if(b.getHeight() >= overlay.getHeight())
            height = overlay.getHeight();
        else
            height = b.getHeight();

        if(b.getWidth() >= overlay.getWidth())
            width = overlay.getWidth();
        else
            width = b.getWidth();

        int rgb;
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                rgb = overlay.getRGB(i, j);
                if ((rgb >> 24 & 0xff) != 0)
                    b.setRGB(i, j, rgb); // only overlay non-transparent pixels
            }
        }
        return b;
    }
}
