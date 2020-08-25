package com.plainsimple.spaceships.sprite;

import android.content.Context;

import com.plainsimple.spaceships.engine.GameContext;
import com.plainsimple.spaceships.helper.BitmapData;
import com.plainsimple.spaceships.store.RocketType;

import java.util.NoSuchElementException;

/**
 * A superclass for all rocket types. Initializing Rockets
 * must be done through the newInstance method, which will
 * return a new instance of the correct subclass.
 */
public abstract class Rocket extends Sprite {

    // static method used to create a new instance of Rocket. Uses rocketType
    // to determine which subclass to instantiate. This allows the spaceship
    // to get the Rocket instance without needing to know which subclass to
    // call.
    public static Rocket newInstance(GameContext gameContext, float x, float y, RocketType rocketType) {
        switch (rocketType) {
            case ROCKET_0:
                return new Rocket0(gameContext, x, y);
            case ROCKET_1:
                return new Rocket1(gameContext, x, y);
            case ROCKET_2:
                return new Rocket2(gameContext, x, y);
            case ROCKET_3:
                return new Rocket3(gameContext, x, y);
            default:
                throw new NoSuchElementException("Did not recognize RocketType " + rocketType);
        }
    }

    protected Rocket(BitmapData bitmapData, float x, float y, GameContext gameContext) {
        super(x, y, bitmapData, gameContext);
    }


}
