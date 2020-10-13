package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.helper.BitmapCache;
import com.plainsimple.spaceships.helper.BitmapID;
import com.plainsimple.spaceships.helper.SpriteAnimation;

/**
 * Created by Stefan on 8/31/2020.
 */

public class AnimFactory {

    private BitmapCache bitmapCache;

    public AnimFactory(BitmapCache bitmapCache) {
        this.bitmapCache = bitmapCache;
    }

    public SpriteAnimation get(AnimID animationID) {
        // TODO: IDEALLY WE'D READ SOME KIND OF CONFIGURATION FILE
        switch (animationID) {
            case SPACESHIP_MOVE: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.SPACESHIP_MOVE),
                        new int[]{150, 150},
                        true
                );
            }
            case SPACESHIP_EXPLODE: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.SPACESHIP_EXPLODE),
                        new int[]{150, 150, 150, 150, 150, 150, 150, 150},
                        false
                );
            }
            case COIN_SPIN: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.COIN_SPIN),
                        new int[]{120, 120, 120, 120, 120, 120},
                        true
                );
            }
            // NOTE: CURRENTLY, WE JUST RE-USE THE SPACESHIP EXPLOSION ANIMATION (TODO)
            case ALIEN_EXPLODE: {
                return new SpriteAnimation(
                        bitmapCache.getData(BitmapID.SPACESHIP_EXPLODE),
                        new int[]{150, 150, 150, 150, 150, 150, 150, 150},
                        false
                );
            }
            default: {
                throw new IllegalArgumentException(
                        String.format("Unsupported AnimID %s", animationID.toString())
                );
            }
        }
    }
}
