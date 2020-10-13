package com.plainsimple.spaceships.helper;

import plainsimple.spaceships.R;

/**
 * All bitmaps used by the game must have a BitmapID, which stores the
 * R.drawable id associated with a bitmap's title. This is to standardize
 * the way bitmaps are referenced and to ensure only valid R.drawable id's
 * are used.
 */
public enum BitmapID {
    SPACESHIP(R.drawable.spaceship),
    SPACESHIP_BASE(R.drawable.spaceship_base),
    SPACESHIP_EXPLODE(R.drawable.spaceship_explode),
    SPACESHIP_FIRE(R.drawable.spaceship_fire_rocket),
    SPACESHIP_MOVE(R.drawable.spaceship_move),
    BULLET_0(R.drawable.bullet_0),
    CANNONS_0(R.drawable.cannons_0),
    BULLET_1(R.drawable.bullet_1),
    CANNONS_1(R.drawable.cannons_1),
    BULLET_2(R.drawable.bullet_2),
    CANNONS_2(R.drawable.cannons_2),
    BULLET_3(R.drawable.bullet_3),
    CANNONS_3(R.drawable.cannons_3),
    ROCKET_0(R.drawable.rocket_0),
    ROCKET0_OVERLAY(R.drawable.rocket0_overlay),
    ROCKET_1(R.drawable.rocket_1),
    ROCKET1_OVERLAY(R.drawable.rocket1_overlay),
    ROCKET_2(R.drawable.rocket_2),
    ROCKET2_OVERLAY(R.drawable.rocket2_overlay),
    ROCKET_3(R.drawable.rocket_3),
    ROCKET3_OVERLAY(R.drawable.rocket3_overlay),
    ROCKET_MOVE(R.drawable.rocket_move),
    PROJECTILE_EXPLODE(R.drawable.projectile_explode),
    EXPLOSION_1(R.drawable.exp_1),
    ALIEN(R.drawable.alien),
    ALIEN_BULLET(R.drawable.alienbullet),
    ASTEROID(R.drawable.asteroid),
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
