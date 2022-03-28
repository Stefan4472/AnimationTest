package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.engine.draw.DrawInstruction;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 9/1/2020.
 */

public class DrawLayers {
    private int numLayers;
    private List<Sprite>[] layers;  // TODO: NAMING IS HORRIBLE


    // TODO: HAVE A WAY TO ASSIGN A DRAW LAYER TO A SPRITE TYPE
    public DrawLayers(int numLayers) {
        this.numLayers = numLayers;
        layers = new LinkedList[numLayers];
        // TODO: `FASTLIST` STRUCTURE?
        for (int i = 0; i < numLayers; i++){
            layers[i] = new LinkedList<>();
        }
    }

    public void addSprite(Sprite sprite) {
        layers[sprite.getDrawLayer()].add(sprite);
    }

    public void getDrawInstructions(
            ProtectedQueue<DrawInstruction> drawQueue,
            boolean drawHitboxes  // TODO: use a globally-set DEBUG flag
    ) {
        for (List<Sprite> sprite_layer : layers) {
            for (Sprite sprite : sprite_layer) {
                sprite.getDrawInstructions(drawQueue);
                if (drawHitboxes) {
                    // TODO: this is unclean
                    drawQueue.push(sprite.drawHitbox());
                }
            }
        }
    }

    public void clear() {
        for (List<Sprite> sprite_layer : layers) {
            sprite_layer.clear();
        }
    }
}
