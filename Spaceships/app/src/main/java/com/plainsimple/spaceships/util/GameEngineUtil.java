package com.plainsimple.spaceships.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.sprite.Alien;
import com.plainsimple.spaceships.sprite.Sprite;

import java.util.Iterator;
import java.util.List;

/**
 * Static methods used in game loop
 */
public class GameEngineUtil {

    public static void updateSprites(List<Sprite> toUpdate) {
        Iterator<Sprite> i = toUpdate.iterator(); // todo: get all sprites together, collisions, etc.
        while(i.hasNext()) {
            Sprite s = i.next();
            s.updateActions();
            s.updateSpeeds(); // todo: hit detection
            s.move();
            s.updateAnimations();
            if(s.terminate()) { // todo: doesn't remove sprites that are out of bounds
                i.remove();
            }
        }
    }

    static List<DrawParams> drawParams;
    // draws sprite onto canvas using sprite drawing params and imageCache
    public static void drawSprite(Sprite sprite, Canvas canvas, Context context) {
        drawParams = sprite.getDrawParams();
        for (DrawParams p : drawParams) {
            p.draw(canvas, context);
        }
        /*if (sprite.collides()) {
            canvas.drawRect(sprite.getHitBox(), debugPaintRed);
        } else {
            canvas.drawRect(sprite.getHitBox(), debugPaintPink);
        }*/
    }
    // goes through sprites, and for each alien uses getAndClearProjectiles,
    // adds those projectiles to projectiles list
    public static void getAlienBullets(List<Sprite> projectiles, List<Sprite> sprites) {
        for(Sprite s : sprites) {
            if (s instanceof Alien) {
                projectiles.addAll(((Alien) s).getAndClearProjectiles());
            }
        }
    }

    // checks sprite against each sprite in list
    // calls handleCollision method if a collision is detected
    public static void checkCollisions(Sprite sprite, List<Sprite> toCheck) {
        // return immediately if sprite does not collide
        if (!sprite.collides()) {
            return;
        } else {
            for (Sprite s : toCheck) { // todo: keep checking if sprite.collides() becomes false? What if three objects collide simultaneously?
                if (sprite.collidesWith(s)) {
                    sprite.handleCollision(s);
                    s.handleCollision(sprite);
                }
            }
        }
    }
}
