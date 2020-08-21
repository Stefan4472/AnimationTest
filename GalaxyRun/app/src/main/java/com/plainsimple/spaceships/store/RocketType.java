package com.plainsimple.spaceships.store;

import com.plainsimple.spaceships.helper.BitmapID;

/**
 * Created by Stefan on 8/27/2016.
 */
public enum RocketType {

    ROCKET_0(BitmapID.ROCKET_0, BitmapID.ROCKET0_OVERLAY),
    ROCKET_1(BitmapID.ROCKET_1, BitmapID.ROCKET1_OVERLAY),
    ROCKET_2(BitmapID.ROCKET_2, BitmapID.ROCKET2_OVERLAY),
    ROCKET_3(BitmapID.ROCKET_3, BitmapID.ROCKET3_OVERLAY);

    // BitmapID of fired bullet when it is shown on screen
    private BitmapID drawableId;
    // BitmapID of spaceship indicator overlay
    private BitmapID spaceshipOverlayId;

    RocketType(BitmapID drawableId, BitmapID spaceshipOverlayId) {
        this.drawableId = drawableId;
        this.spaceshipOverlayId = spaceshipOverlayId;
    }

    public BitmapID getDrawableId() {
        return drawableId;
    }

    public BitmapID getSpaceshipOverlayId() {
        return spaceshipOverlayId;
    }
}
