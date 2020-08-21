package com.plainsimple.spaceships.helper;

import android.graphics.Rect;

/**
 * Created by Stefan on 8/13/2015.
 */
public class SpriteAnimation { // todo: pause (returns same frame each time) and reverse methods

    private BitmapID bitmapID;

    // dimensions of each frame
    private int frameW;
    private int frameH;

    // number of frames in loop
    private int numFrames;

    // frameSpeed of each frame, stored in array
    private int[] frameSpeeds;

    // whether or not to loop animation
    private boolean loop;

    // whether or not this animation is playing
    private boolean isPlaying = false;

    // whether or not animation has already played
    private boolean hasPlayed = false;

    // current position in array of frames
    private int frameCounter = 0;

    // counts number of frames current sprite has been shown
    private int frameSpeedCounter = 0;

    // takes R.id of Bitmap representing spritesheet, which consists of one row of sprites
    // initializes all frames now so as to cut down on processing time later
    public SpriteAnimation(BitmapData bitmapData, int[] frameSpeeds, boolean loop) { // todo: confusion btwn. frameW and bitmapData.getWidth()
        bitmapID = bitmapData.getId();
        this.frameSpeeds = frameSpeeds;
        numFrames = frameSpeeds.length;
        frameW = bitmapData.getWidth() / numFrames;
        frameH = bitmapData.getHeight();
        this.loop = loop;
    }

    // resets fields so that nextFrame() can play animation
    public void start() { // todo: good practice is to start animation before calling nextFrame()
        isPlaying = true;
        frameCounter = 0;
        frameSpeedCounter = 0;
    }

    // stops animation
    public void stop() {
        isPlaying = false;
    }

    public void reset() {
        frameCounter = 0;
        frameSpeedCounter = 0;
        hasPlayed = false;
        isPlaying = false;
    }

    // whether animation has finished or not
    public boolean isPlaying() {
        return isPlaying;
    }

    // whether animation has played
    public boolean hasPlayed() {
        return hasPlayed;
    }

    // progresses frame counter by one
    public void incrementFrame() {
        if (frameSpeedCounter == frameSpeeds[frameCounter]) {
            frameCounter++;
            frameSpeedCounter = 1;
        } else {
            frameSpeedCounter++;
        }

        // reached end of loop
        if(!loop && frameCounter == numFrames) {
            hasPlayed = true;
            isPlaying = false;
        } else if(loop && frameCounter == numFrames) {
            hasPlayed = true;
            frameCounter = 0;
        }
    }

    // returns Rect containing coordinates on the spritesheet of the current frame
    // -> all images are treated in reference to their ID
    public Rect getCurrentFrameSrc() {
        return new Rect(frameW * frameCounter, 0, frameW * (frameCounter + 1), frameH);
    }

    public BitmapID getBitmapID() {
        return bitmapID;
    }

    public int getFrameW() {
        return frameW;
    }

    public int getFrameH() {
        return frameH;
    }

    // return number of frames left in animation (not total number of frameCounts!!)
    public int getFramesLeft() {
        return numFrames - frameCounter;
    }

    // return total frame count of animation
    public int getFrameCount() {
        int total = 0;
        for (int count : frameSpeeds) {
            total += count;
        }
        return total;
    }

    public String getDebug() {
        return ("Frame " + frameCounter + " of " + numFrames + " with current speed at " + frameSpeedCounter + " of " + frameSpeeds[frameCounter] +
                " " + (isPlaying ? "(playing)" : "(stopped)") + (loop ? " (looping)" : ""));
    }
}
