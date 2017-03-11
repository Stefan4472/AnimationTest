package com.plainsimple.spaceships.helper;

import plainsimple.spaceships.R;

/**
 * Represent bitmap resources in R.drawable
 */
public enum BitmapID {
    SPACESHIP(R.drawable.spaceship),
    SPACESHIP_BASE(R.drawable.spaceship_base),
    SPACESHIP_EXPLODE(R.drawable.spaceship_explode),
    SPACESHIP_FIRE(R.drawable.spaceship_fire_rocket),
    SPACESHIP_MOVE(R.drawable.spaceship_move),
    LASER_BULLET(R.drawable.laserbullet),
    LASER_CANNONS(R.drawable.laser_cannons),
    ION_BULLET(R.drawable.ionbullet),
    ION_CANNONS(R.drawable.ion_cannons),
    PLASMA_CANNONS(R.drawable.plasma_cannons),
    PLUTONIUM_CANNONS(R.drawable.plutonium_cannons),
    ROCKET(R.drawable.rocket),
    ROCKET_OVERLAY(R.drawable.rocket1_overlay),
    ROCKET_MOVE(R.drawable.rocket_move),
    PROJECTILE_EXPLODE(R.drawable.projectile_explode),
    ALIEN(R.drawable.alien),
    ALIEN_BULLET(R.drawable.alienbullet),
    COIN(R.drawable.coin),
    COIN_SPIN(R.drawable.coin_spin),
    OBSTACLE(R.drawable.obstacle);

    // r.drawable id specifying bitmap
    private int rId;

    public int getrId() {
        return rId;
    }

    BitmapID(int rId) {
        this.rId = rId;
    }
}
