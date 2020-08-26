package com.plainsimple.spaceships.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.engine.UpdateContext;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.DrawRect;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Sprite;

import java.util.Iterator;
import java.util.List;

/**
 * Static methods used in game loop
 */
public class GameEngineUtil {

    private static Paint debugPaintRed = initRedPaint();
    private static Paint debugPaintPink = initPinkPaint();

    private static Paint initRedPaint() {
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.STROKE);
        red.setStrokeWidth(3);
        red.setTextSize(20);
        return red;
    }

    private static Paint initPinkPaint() {
        Paint pink = new Paint();
        pink.setColor(Color.rgb(255, 105, 180));
        pink.setStyle(Paint.Style.STROKE);
        pink.setStrokeWidth(3);
        return pink;
    }

    // runs full update on each sprite in given list
    // this includes, in order, updating actions, speeds,
    // coordinates, and animations. Removes sprite from list
    // if terminate = true after all updating. Note: sprites
    // must take care of their own terminate logic. Collisions
    // are not tested in this method.
    public static void updateSprites(List<Sprite> toUpdate, UpdateContext updateContext) {
        Iterator<Sprite> i = toUpdate.iterator(); // todo: get all sprites together, collisions, etc.
        while(i.hasNext()) {
            Sprite sprite = i.next();
            updateSprite(sprite, updateContext);
            if(sprite.terminate()) {
                i.remove();
            }
        }
    }

    public static void updateSprite(Sprite sprite, UpdateContext updateContext) {
        sprite.updateSpeeds();
        sprite.move();
        sprite.updateActions(updateContext);
        sprite.updateAnimations();
    }
    private static List<DrawParams> drawParams;
    private static DrawRect DRAW_HITBOX = new DrawRect(debugPaintRed.getColor(), debugPaintRed.getStyle(), debugPaintRed.getStrokeWidth());
    // draws sprite onto canvas using sprite drawing params and BitmapCache
//    public static void drawSprite(Sprite sprite, Canvas canvas, GameContext gameContext) {
//        drawParams = sprite.getDrawParams();
//        for (DrawParams p : drawParams) {
//            p.draw(canvas, gameContext);
//        }
//        // draw hitbox (debugging)
//        if (sprite.collides()) {
//            DRAW_HITBOX.setBounds(sprite.getHitBox());
//            DRAW_HITBOX.draw(canvas, gameContext);
//        }
//    }

    // checks sprite against each sprite in list
    // calls handleCollision method if a collision is detected
    // informs sprite how much damage other sprite had at instant of collision
    public static void checkCollisions(Sprite sprite, List<Sprite> toCheck) {
        // return immediately if sprite does not collide
        if (!sprite.collides()) {
            return;
        } else {
            for (Sprite s : toCheck) {
                if (sprite.collidesWith(s)) {
                    int sprite_damage = sprite.getHP();
                    int s_damage = s.getHP();
                    sprite.handleCollision(s, s_damage);
                    s.handleCollision(sprite, sprite_damage);
                }
            }
        }
    }
}
