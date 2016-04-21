package plainsimple.spaceships;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/13/2015.
 */
public class Rocket extends Sprite {

    // todo: different rocket types (?)
    public final static int ROCKET = 1;

    // number of to delay between firing rockets
    public int delay;

    // points awarded for scoring a hit
    private int score;

    public static final int ROCKET_DAMAGE = 40;
    public static final int ROCKET_DELAY = 40;
    public static final float ROCKET_SPEED_X = 0.0067f;

    public Rocket(BitmapData bitmapData, float x, float y, int rocketType) {
        super(bitmapData, x, y);
        hitBox.setDimensions((int) (getWidth() * 1.5f), getHeight());
        switch(rocketType) {
            case ROCKET:
                delay = ROCKET_DELAY;
                speedX = ROCKET_SPEED_X;
                damage = ROCKET_DAMAGE;
                break;
        }
    }

    public static int getDelay(int rocketType) {
        switch (rocketType) {
            case ROCKET:
                return ROCKET_DELAY;
            default:
                return -1;
        }
    }

    // returns score and resets it to zero
    public int getAndClearScore() {
        int score_copy = score;
        score = 0;
        return score_copy;
    }

    @Override
    public void updateActions() {

    }

    @Override
    public void updateSpeeds() { // todo: relative speeds

    }

    @Override
    public void handleCollision(Sprite s) {
        collision = true;
        if (s instanceof Alien) {
            score += damage;
        }
        vis = false;

    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public ArrayList<int[]> getDrawParams() {
        ArrayList<int[]> params = new ArrayList<>();
        params.add(new int[] {bitmapData.getId(), (int) x, (int) y, 0, 0, getWidth(), getHeight()});
        return params;
    }
}
