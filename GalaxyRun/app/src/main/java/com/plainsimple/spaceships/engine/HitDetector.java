package com.plainsimple.spaceships.engine;

import android.content.res.ColorStateList;

import com.plainsimple.spaceships.sprite.Sprite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 9/1/2020.
 */
// TODO: VERY SIMPLE AT THE MOMENT, WIP
// TODO: MAKE CHUNK-BASED
public class HitDetector {
    // Stores the references of two sprites that are in collision
    public static class CollisionTuple {
        public Sprite sprite1, sprite2;

        public CollisionTuple(Sprite sprite1, Sprite sprite2) {
            this.sprite1 = sprite1;
            this.sprite2 = sprite2;
        }
    }

    public static class CollisionLayer {
        public int layer;
        public int[] collidingLayers;

        public CollisionLayer(int layer, int[] collidingLayers) {
            this.layer = layer;
            this.collidingLayers = collidingLayers;
        }
    }

    private int numLayers;
    private List<Sprite>[] layers;  // TODO: NAMING IS HORRIBLE
    private CollisionLayer[] collisionLayers;

    public HitDetector(CollisionLayer[] collisionLayers) {
        this.collisionLayers = collisionLayers;
        numLayers = collisionLayers.length;
        layers = new LinkedList[numLayers];
        // TODO: `FASTLIST` STRUCTURE?
        for (int i = 0; i < numLayers; i++){
            layers[i] = new LinkedList<>();
        }
    }

    public void addSprite(Sprite sprite) {
        layers[sprite.getCollisionLayer()].add(sprite);
    }

    public List<CollisionTuple> determineCollisions() {
        List<CollisionTuple> collisions = new LinkedList<>();
        for (CollisionLayer layer_def : collisionLayers) {
            for (int colliding_layer : layer_def.collidingLayers) {
                checkCollisionsBtwnLayers(
                        layer_def.layer,
                        colliding_layer,
                        collisions
                );
            }
        }
        return collisions;
    }

    public void clear() {
        for (List<Sprite> layer : layers) {
            layer.clear();
        }
    }

    private void checkCollisionsBtwnLayers(
            int layerOne,
            int layerTwo,
            List<CollisionTuple> collisionsList
    ) {
        List<Sprite> spritesLayerOne = layers[layerOne];
        List<Sprite> spritesLayerTwo = layers[layerTwo];

        for (Sprite l1_sprite : spritesLayerOne) {
            if (l1_sprite.canCollide()) {
                for (Sprite l2_sprite : spritesLayerTwo) {
                    if (l1_sprite.collidesWith(l2_sprite)) {
                        collisionsList.add(new CollisionTuple(
                                l1_sprite,
                                l2_sprite
                        ));
                    }
                }
            }
        }
    }
}
