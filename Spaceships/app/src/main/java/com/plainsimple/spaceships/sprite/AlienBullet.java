package com.plainsimple.spaceships.sprite;

import android.graphics.Rect;
import android.util.Log;

import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.helper.DrawImage;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawRotatedImage;
import com.plainsimple.spaceships.helper.Hitbox;
import com.plainsimple.spaceships.helper.Point2D;
import com.plainsimple.spaceships.view.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * An AlienBullet is a projectile fired by an Alien at a
 * certain set of coordinates (specified in the constructor).
 * The AlienBullet will determine its own speedX and speedY
 * to reach the target. It will also request to be drawn at
 * the correct angle to reach its target.
 */
public class AlienBullet extends Sprite {

    // angle that bullet travels
    private float fireAngle;

    public AlienBullet(BitmapData bitmapData, float x, float y, Point2D target) { // todo: damage as a parameter?
        super(bitmapData, x, y);
        hitBox = new Hitbox(x, y, x + getWidth(), y + getHeight());
        hp = 10;
        // speedX is fixed
        speedX = -0.004f;// - random.nextInt(5) / 10000.0f;
        // calculate fireAngle based on distance to target in x and y
        float dist_x = x - target.getX();
        float dist_y = y - target.getY();
        fireAngle = (float) -Math.atan(dist_x / dist_y);
        // calculate speedY using relative screen dimensions
        float frames_to_impact = (dist_x / GameView.screenW) / speedX;
        speedY = (dist_y / GameView.screenH) / frames_to_impact;
    }

    @Override
    public void updateActions() {
        if (!isInBounds()) {
            terminate = true;
//            Log.d("Termination", "Removing AlienBullet at x = " + x);
        }
    }

    @Override
    public void updateSpeeds() {

    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        if (!(s instanceof Alien)) {
            collides = false;
            terminate = true;
        }
        //if (s instanceof Spaceship) {

        //}
    }

    @Override
    public List<DrawParams> getDrawParams() {
        drawParams.clear();
        drawParams.add(new DrawRotatedImage(bitmapData.getId(), x, y,
                (int) fireAngle, x + getWidth() / 2, y + getHeight() / 2));
        return drawParams;
    }
}
