package com.plainsimple.spaceships.helper;

/**
 * The classes that implement this abstract class are used to
 * manage the firing of rockets by the Spaceship. The attemptFire
 * method takes the spaceship's frame count
 * (number of frames it has been in existence) and returns
 * which rockets may be fired (using the FireInstructions wrapper).
 * This allows different RocketTypes to have different fire patterns.
 * Note: The Spaceship class is responsible for keeping
 * track of its frame count. ALSO NOTE: a return of TRUE assumes
 * the rocket was fired, and resets the count. Only call with the
 * intent to fire.
 */

public abstract class RocketManager {

    public abstract FireInstructions attemptFire(int frameCount);

    // Straightforward wrapper class to send booleans for firing
    // left rocket and/or right rocket
    public class FireInstructions {

        private boolean fireLeft, fireRight;

        public boolean fireLeft() {
            return fireLeft;
        }

        public boolean fireRight() {
            return fireRight;
        }

        public FireInstructions(boolean fireLeft, boolean fireRight) {
            this.fireLeft = fireLeft;
            this.fireRight = fireRight;
        }
    }
}
