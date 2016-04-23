package plainsimple.spaceships;

import java.util.ArrayList;

/**
 * Created by Stefan on 8/20/2015.
 */
public class Obstacle extends Sprite {

    public Obstacle(BitmapData bitmapData, float x, float y) {
        super(bitmapData, x, y);
        initObstacle();
    }

    private void initObstacle() {
        hitBox.setDimensions(getWidth(), getHeight());
        damage = Integer.MAX_VALUE;
    }

    @Override
    public void updateActions() {

    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s) {

    }

    @Override
    public ArrayList<int[]> getDrawParams() {
        ArrayList<int[]> params = new ArrayList<>();
        params.add(new int[] {bitmapData.getId(), 0, 0, getWidth(), getHeight()});
        return params;
    }
}
