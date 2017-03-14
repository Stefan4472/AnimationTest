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

    // runs full update on each sprite in given list
    // this includes, in order, updating actions, speeds,
    // coordinates, and animations. Removes sprite from list
    // if terminate = true after all updating. Note: sprites
    // must take care of their own terminate logic. Collisions
    // are not tested in this method.
    public static void updateSprites(List<Sprite> toUpdate) {
        Iterator<Sprite> i = toUpdate.iterator(); // todo: get all sprites together, collisions, etc.
        while(i.hasNext()) {
            Sprite s = i.next();
            s.updateActions();
            s.updateSpeeds();
            s.move();
            s.updateAnimations();
            if(s.terminate()) {
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

    // goes through sprites, casts each to Alien and uses getAndClearProjectiles
    // to get their projectiles and add them to the given list.
    // WARNING! do not use this method on non-Aliens
    public static void getAlienBullets(List<Sprite> projectiles, List<Sprite> sprites) {
        for(Sprite s : sprites) {
            projectiles.addAll(((Alien) s).getAndClearProjectiles());
        }
    }

    // checks sprite against each sprite in list
    // calls handleCollision method if a collision is detected
    // automatically cross-subtracts the hp's of each sprite
    public static void checkCollisions(Sprite sprite, List<Sprite> toCheck) {
        // return immediately if sprite does not collide
        if (!sprite.collides()) {
            return;
        } else {
            for (Sprite s : toCheck) {
                if (sprite.collidesWith(s)) {
                    int sprite_damage = sprite.getHP();
                    int s_damage = s.getHP();
                    sprite.takeDamage(s_damage);
                    s.takeDamage(sprite_damage);
                    sprite.handleCollision(s, s_damage);
                    s.handleCollision(sprite, sprite_damage);
                }
            }
        }
    }
}
