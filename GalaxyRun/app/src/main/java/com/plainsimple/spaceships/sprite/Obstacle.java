package com.plainsimple.spaceships.sprite;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawRect;
import com.plainsimple.spaceships.helper.FloatRect;
import com.plainsimple.spaceships.util.ProtectedQueue;
import com.plainsimple.spaceships.view.GameView;

import java.util.List;

/**
 * The Obstacle is a basic sprite that looks like a regular gray rectangle. Because there it has no
 * image, its hitbox is used to determine both its collision area as well as drawing bounds.
 *
 * The Obstacle only responds to collisions with the Spaceship. In this case it does base damage,
 * as determined by what it originally was set to. Then, each frame it collides with the spaceship
 * it does that amount of damage plus a small amount added each frame (so damage increases each frame).
 * This way if the player reacts quickly enough, the damage may not be too deadly.
 */
public class Obstacle extends Sprite {

    // color of obstacle
    private int color = Color.rgb(103, 103, 103);

    public Obstacle(int spriteId, float x, float y, int width, int height, GameContext gameContext) {
        super(spriteId, SpriteType.OBSTACLE, x, y, width, height, gameContext);
        hitBox = new FloatRect(x, y, x + getWidth(), y + getHeight());
        hp = 5;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        // terminate when hitBox is out of bounds to the left of the screen
        if (x < -hitBox.getWidth()) {
            terminate = true;
        }
    }

    @Override // speedX is set to the game's scrollspeed to ensure
    // smooth acceleration and decelleration with the game
    public void updateSpeeds() {
//        speedX = GameView.getScrollSpeed();
    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s, int damage) {
        if (s instanceof Spaceship) {
            hp += 2; // todo: too high?
        }
    }

    // init DrawRect instance with specified color and fill Paint Style
    private DrawRect DRAW_OBSTACLE = new DrawRect(color, Paint.Style.FILL, 1);
    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        // todo: only draw what's on screen
        DRAW_OBSTACLE.setBounds(hitBox);
        drawQueue.push(DRAW_OBSTACLE);
    }
}
