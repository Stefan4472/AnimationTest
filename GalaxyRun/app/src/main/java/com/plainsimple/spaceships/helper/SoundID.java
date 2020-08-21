package com.plainsimple.spaceships.helper;

import plainsimple.spaceships.R;

/**
 * Stores ID's for referring to raw resources
 */
public enum SoundID {
    LASER(R.raw.laser_fired),
    ROCKET(R.raw.rocket_fired),
    EXPLOSION(R.raw.explosion_1),
    BUTTON_CLICKED(R.raw.button_clicked);

    // r.raw id specifying sound
    private int rId;

    SoundID(int rId) {
        this.rId = rId;
    }

    public int getId() {
        return rId;
    }
}
