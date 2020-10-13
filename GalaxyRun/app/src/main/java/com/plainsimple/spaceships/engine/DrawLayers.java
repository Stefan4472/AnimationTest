package com.plainsimple.spaceships.engine;

import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.sprite.Sprite;
import com.plainsimple.spaceships.util.GameEngineUtil;
import com.plainsimple.spaceships.util.ProtectedQueue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 9/1/2020.
 */

public class DrawLayers {
    private int numLayers;
    private List<Sprite>[] layers;  // TODO: NAMING IS HORRIBLE


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

    // TODO: RETURN A NEW LIST OR SOMETHING
    public void getDrawParams(
            ProtectedQueue<DrawParams> drawQueue,
            boolean drawHitboxes
    ) {
        for (List<Sprite> sprite_layer : layers) {
            for (Sprite sprite : sprite_layer) {
                sprite.getDrawParams(drawQueue);
                if (drawHitboxes) {
                    drawQueue.push(GameEngineUtil.drawHitbox(sprite));
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
