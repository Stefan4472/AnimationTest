package plainsimple.spaceships;

import android.graphics.Canvas;
import android.util.Log;

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
            s.move();
            if(s.isInBounds() && s.isVisible()) {
                s.updateActions();
                s.updateSpeeds(); // todo: hit detection
                s.updateAnimations();
            } else {
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
        for(Sprite s : toCheck) {
            if(sprite.collidesWith(s)) {
                sprite.handleCollision(s);
                s.handleCollision(sprite);
                if (sprite instanceof Alien1) {
                    Log.d("GameEngine Class", "Alien Collision Detected");
                }
            }
        }
    }
}
