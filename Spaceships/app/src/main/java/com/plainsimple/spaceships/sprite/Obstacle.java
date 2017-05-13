package com.plainsimple.spaceships.sprite;

import android.graphics.Color;
import android.graphics.Paint;

import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawRect;
import com.plainsimple.spaceships.helper.FloatRect;
import com.plainsimple.spaceships.view.GameView;

import java.util.List;

/**
 * The Obstacle is a basic sprite that looks like a regular gray rectangle.
 * The Obstacle has very high hp, because it is meant to automatically destroy
 * the spaceship on impact. It is represented by its hitbox.
 */
public class Obstacle extends Sprite {

    // color of obstacle
    private int color = Color.rgb(103, 103, 103);

    public Obstacle(float x, float y, int width, int height) {
        super(x, y, width, height);
        hitBox = new FloatRect(x, y, x + getWidth(), y + getHeight());
        hp = 10_000; // todo: some impossible to reach number?
    }

    @Override
    public void updateActions() {
        // terminate when hitBox is out of bounds to the left of the screen
        if (x < -hitBox.getWidth()) {
            terminate = true;
        }
    }

    @Override // speedX is set to the game's scrollspeed to ensure
    // smooth acceleration and decelleration with the game
    public void updateSpeeds() {
        speedX = GameView.getScrollSpeed();
    }

    @Override
    public void updateAnimations() {

    }

    @Override
    public void handleCollision(Sprite s, int damage) {

    }

    @Override // todo: reuse drawparam to ease performance
    public List<DrawParams> getDrawParams() { // todo: only draw what's on screen
        drawParams.clear();
        drawParams.add(new DrawRect(x, y, x + hitBox.getWidth(), y + hitBox.getHeight(), color, Paint.Style.FILL, 1));
        return drawParams;
    }
}
