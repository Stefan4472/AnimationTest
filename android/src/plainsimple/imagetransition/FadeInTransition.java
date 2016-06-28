package plainsimple.imagetransition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import plainsimple.spaceships.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stefan on 6/28/2016.
 */
public class FadeInTransition extends ImageTransition {

    // two-dimensional array representing all pixels of image
    private List<Point2D> pixels;

    // counter for number of pixels already revealed
    private int revealed;

    // paint used while drawing pixels
    private Paint paint;

    public FadeInTransition(Bitmap startImage, Bitmap endImage, int totalFrames) {
        super(startImage, endImage, totalFrames);
        pixels = new ArrayList<>();
        // create a shuffled list of all pixels in the image
        for (int i = 0; i < this.startImage.getWidth(); i++) {
            for (int j = 0; j < this.startImage.getHeight(); j++) {
                pixels.add(new Point2D(i, j));
            }
        }
        Collections.shuffle(pixels);
        revealed = 0;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawFrame(float completion, Canvas canvas) {
        // number of pixels revealed as of last frame
        int last_revealed = revealed;
        // calculate number of pixels revealed once this frame is drawn
        revealed = (int) (pixels.size() * completion);
        // coordinates of pixel to be drawn
        Point2D pixel;
        for (int i = last_revealed; i < revealed; i++) {
            pixel = pixels.get(i);
            paint.setColor(startImage.getPixel(pixel.getX(), pixel.getY()));
            canvas.drawPoint(pixel.getX(), pixel.getY(), paint);
        }
    }
}
