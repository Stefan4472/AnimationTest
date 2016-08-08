package plainsimple.spaceships.util;

import android.util.Log;
import plainsimple.spaceships.sprites.Alien;
import plainsimple.spaceships.sprites.Sprite;

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
                if (s instanceof Alien) {
                    Log.d("GameEngine", "Removing Alien where inBounds = " + s.isInBounds());
                }
                i.remove();
            }
        }
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
