import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stefan on 8/13/2015.
 */
public class SpriteAnimation {

    // whether or not to loop animation
    private boolean loop;

    // whether or not this animation is playing
    private boolean isPlaying;

    // frames of the animation to play in order
    private Image[] frames;

    // current position in array of frames
    private int frameCounter;

    // number of frames to display each sprite
    private int frameSpeed;

    // counts number of frame current sprite has been shown
    private int frameSpeedCounter;

    public SpriteAnimation(Image[] frames, boolean loop) {
        this.frames = frames;
        this.loop = loop;
        frameCounter = 0;
    }

    // reads in spritesheet consisting of one row of sprites
    // initializes all frames now so as to cut down on processing time later
    public SpriteAnimation(String spriteSheetPath, int frameWidth,
                           int frameHeight, int frameSpeed, boolean loop) throws IOException {

        BufferedImage sheet = ImageIO.read(new File(spriteSheetPath));

        int frames_w = sheet.getWidth(null) / frameWidth;
        int frames_h = sheet.getHeight(null) / frameHeight;

        frames = new Image[frames_w * frames_h];

        for(int i = 0; i < frames_w; i++) {
            for(int j = 0; j < frames_h; j++) {
                frames[i] = sheet.getSubimage(i * frameWidth, j * frameHeight, frameWidth, frameHeight);
            }
        }
        this.loop = loop;
    }

    // converts files to images
    public SpriteAnimation(File[] frames, boolean loop) {
        this.frames = new Image[frames.length];

        for(int i = 0; i < frames.length; i++) {
            this.frames[i] = new ImageIcon(frames[i].getName()).getImage();
        }
        this.loop = loop;
    }

    // starts animation, returns first frame
    public Image start() {
        isPlaying = true;
        frameCounter = 0;
        return frames[0];
    }

    // whether animation has finished or not
    public boolean isPlaying() {
        return isPlaying;
    }

    // returns next image in animation
    public Image nextFrame() throws IndexOutOfBoundsException {
        frameCounter++;
        if(loop) {
            // reached end of loop, start from beginning
            if(frameCounter == frames.length) {
                frameCounter = 0;
            }
        } else {
            // reached end of loop
            if(frameCounter == frames.length - 1) {
                isPlaying = false;
            }
        }

        return frames[frameCounter];
    }

    // stops animation
    public void stop() {
        isPlaying = false;
        frameCounter = 0;
    }

}
