package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.util.Log;

import plainsimple.spaceships.R;

/**
 * Cache for Sprite Animations. Retrieved using BitmapID // todo: or use AnimCache enum?
 */

public class AnimCache { // todo: use enums?

    // Initializes the specified sprite animation using parameters
    // from R.string. Parameters should be in the form
    // num_frames, followed by the frame counts of each frame,
    // followed by the boolean that determines whether the animation loops.
    // Parameters are separated by the ':' character. BitmapID must be registered
    // in keyToString method.
    // @throws IllegalArgumentException if any error is encountered creating the animation todo: enum??
    public static SpriteAnimation get(BitmapID key, Context context) throws IllegalArgumentException {
        try {
            // convert key to string of params
            String anim_params = keyToString(key, context);
            // parse first argument: number of frames
            int num_frames = Integer.parseInt(anim_params.substring(0, anim_params.indexOf(':')));
            int[] frame_speeds = new int[num_frames];
            // keep track of where we are in parsing the string
            int read_index = anim_params.indexOf(':') + 1;
            for (int i = 0; i < frame_speeds.length; i++) {
                frame_speeds[i] = Integer.parseInt(anim_params.substring(read_index, anim_params.indexOf(':', read_index)));
                read_index = anim_params.indexOf(':', read_index) + 1;
            }
            boolean loop = Boolean.parseBoolean(anim_params.substring(read_index));
            return new SpriteAnimation(BitmapCache.getData(key, context), frame_speeds, loop);
        } catch (Exception e) {
            Log.d("AnimCache.java", "There was an error creating a SpriteAnimation");
            Log.d("AnimCache.java", "Error: " + e.getMessage());
            return null;
        }
    }

    // returns R.string associated with key
    private static String keyToString(BitmapID key, Context context) throws IllegalArgumentException {
        switch (key) {
            case SPACESHIP_MOVE:
                return context.getString(R.string.SPACESHIP_MOVE);
            case SPACESHIP_EXPLODE:
                return context.getString(R.string.SPACESHIP_EXPLODE);
            case SPACESHIP_FIRE:
                return context.getString(R.string.SPACESHIP_FIRE_ROCKET);
            case COIN_SPIN:
                return context.getString(R.string.COIN_SPIN);
            case ROCKET_MOVE:
                return context.getString(R.string.ROCKET_MOVE);
            case PROJECTILE_EXPLODE:
                return context.getString(R.string.PROJECTILE_EXPLODE);
            case EXPLOSION_1:
                return context.getString(R.string.EXPLOSION_1);
            default:
                throw new IllegalArgumentException("No animation corresponds to given key");
        }
    }
}
