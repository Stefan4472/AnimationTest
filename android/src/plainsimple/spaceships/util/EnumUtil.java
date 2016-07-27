package plainsimple.spaceships.util;

import plainsimple.spaceships.R;

/**
 * Converts enums to R.id's
 */
public class EnumUtil {

    public static int getID(BitmapResource key) {
        switch (key) {
            case SPACESHIP:
                return R.drawable.spaceship;
            case SPACESHIP_EXPLODE:
                return R.drawable.spaceship_explode;
            case SPACESHIP_FIRE:
                return R.drawable.spaceship_fire_rocket;
            case SPACESHIP_MOVE:
                return R.drawable.spaceship_move;
            case LASER_BULLET:
                return R.drawable.laserbullet;
            case ION_BULLET:
                return R.drawable.ionbullet;
            case ROCKET:
                return R.drawable.rocket;
            case ALIEN:
                return R.drawable.alien;
            case ALIEN_BULLET:
                return R.drawable.alienbullet;
            case COIN:
                return R.drawable.coin;
            case COIN_SPIN:
                return R.drawable.coin_spin;
            case COIN_DISAPPEAR:
                return R.drawable.coin_collect;
            case OBSTACLE:
                return R.drawable.obstacle;
            default:
                return -1;
        }
    }

    public static int getID(RawResource key) {
        switch (key) {
            case LASER:
                return R.raw.laser_fired;
            case ROCKET:
                return R.raw.rocket_fired;
            case EXPLOSION:
                return R.raw.explosion_1;
            case BUTTON_CLICKED:
                return R.raw.button_clicked;
            case TITLE_THEME:
                return R.raw.title_theme;
            default:
                return -1;
        }
    }
}
