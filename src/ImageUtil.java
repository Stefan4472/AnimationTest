import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by Stefan on 8/17/2015.
 */
public class ImageUtil  {

    // draws as much as overlay as will fit on top of b, starting from top left
    // transparent pixels in the overlay are ignored
    public static BufferedImage layer(BufferedImage b, BufferedImage overlay) { // todo: speedup with WritableRaster?
        int rgb;
        int i = 0, j = 0;
        while(i < b.getHeight() && i < overlay.getHeight()) { // todo: optimizations
            while(j < b.getWidth() && j < overlay.getHeight()) {
                rgb = overlay.getRGB(i, j);
                if((rgb >> 24 & 0xff) != 0)
                    b.setRGB(i, j, rgb);
                j++;
            }
            i++;
        }
        return b;
    }
}
