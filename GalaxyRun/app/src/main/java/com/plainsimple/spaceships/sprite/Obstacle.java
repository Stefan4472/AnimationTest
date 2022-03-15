package com.plainsimple.spaceships.sprite;

import android.graphics.Color;
import android.graphics.Paint;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.engine.draw.DrawParams;
import com.plainsimple.spaceships.engine.draw.DrawRect;
import com.plainsimple.spaceships.util.ProtectedQueue;

/**
 * The Obstacle is a basic sprite that looks like a regular gray rectangle. Because there it has no
 * image, its hitbox is used to determine both its collision area as well as drawing bounds.
 *
 * The Obstacle only responds to collisions with the Spaceship. In this case it does base damage,
 * as determined by what it originally was set to. Then, each frame it canCollide with the spaceship
 * it does that amount of damage plus a small amount added each frame (so damage increases each frame).
 * This way if the player reacts quickly enough, the damage may not be too deadly.
 */
public class Obstacle extends Sprite {

    // Obstacle color
    private int color = Color.rgb(103, 103, 103);
    private DrawRect DRAW_OBSTACLE;

    public static final int OBSTACLE_DAMAGE = 5;

    // TODO: PROVIDE SCROLLSPEED AS AN ARGUMENT. OBSTACLE SPEED SHOULDN'T CHANGE--THAT
    // WAY, THERE WILL BE A NOTICEABLE CHANGE IN SPEEDS AS EACH NEW CHUNK IS SPAWNED
    public Obstacle(
            int spriteId,
            double x,
            double y,
            int width,
            int height,
            GameContext gameContext
    ) {
        super(spriteId, SpriteType.OBSTACLE, x, y, width, height, gameContext);
        setHealth(OBSTACLE_DAMAGE);
        // Init DrawRect instance with specified color and fill Paint Style
        DRAW_OBSTACLE = new DrawRect(color, Paint.Style.FILL, 1);
    }

    @Override
    public int getDrawLayer() {
        return 4;
    }

    @Override
    public void updateActions(UpdateContext updateContext) {
        // terminate when hitBox is out of bounds to the left of the screen
        if (getX() < -getWidth()) {
            setCurrState(SpriteState.TERMINATED);
        }
    }

    @Override // speedX is set to the game's scrollspeed to ensure
    // smooth acceleration and decelleration with the game
    public void updateSpeeds(UpdateContext updateContext) {
//        setSpeedX(-updateContext.getScrollSpeed() * gameContext.getGameWidthPx());
        setSpeedX(-0.1 * gameContext.getGameWidthPx());
    }

    @Override
    public void updateAnimations(UpdateContext updateContext) {

    }

    @Override
    public void handleCollision(Sprite s, int damage, UpdateContext updateContext) {
        if (s.getSpriteType() == SpriteType.SPACESHIP) {
//            hp += 2; // todo: too high?
        }
    }

    @Override
    public void die(UpdateContext updateContext) {

    }

    @Override
    public void getDrawParams(ProtectedQueue<DrawParams> drawQueue) {
        // todo: only draw what's on screen
        DRAW_OBSTACLE.setBounds(getHitbox());
        drawQueue.push(DRAW_OBSTACLE);
    }
}
