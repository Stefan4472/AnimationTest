package plainsimple.spaceships;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Given starting image of screen, creates a "sliding out" animation
 * and feeds frames
 */
public class SlideOutTransition {

    // starting image
    private Bitmap startScreen;
    // current frame
    private int frameCounter = 0;
    // total frames in animation
    private int totalFrames;
    // num rows to slide across screen
    private int numRows;
    // color of rows to overlay
    private Color color;
    // percentage a row should slide across screen before next row starts moving
    private float threshold;

    public SlideOutTransition(Bitmap startScreen, int numRows, int totalFrames) {
        this.startScreen = startScreen;
        this.numRows = numRows;
        this.totalFrames = totalFrames;

    }

    // resets frameCounter to zero
    public void reset() {
        frameCounter = 0;
    }

    // renders and returns next frame in sequence
    public Bitmap nextFrame() {
        frameCounter++;
        return getFrame(frameCounter);
    }

    // renders and returns frame based on completion of sequence
    public Bitmap getFrame(float completion) throws IndexOutOfBoundsException {
        if(completion > 1.0 || completion < 0.0) {
            throw new IndexOutOfBoundsException("Invalid frame requested (" + (totalFrames * completion) + ")");
        } else {
            return getFrame(totalFrames * completion);
        }
    }

    // renders and returns frame based on frameNumber in sequence
    public Bitmap getFrame(int frameNumber) {
        return null;
    }
}
