package com.galaxyrun.engine.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.galaxyrun.sprite.Spaceship;

/*
Takes a real-time stream of gyroscope SensorEvents and turns them into control inputs for the spaceship.

Uses a simple model. Let `Vy` be the angular rotation around the y-axis, as reported by the gyroscope:
- If abs(Vy) < MIN_FILTER, we consider the phone to be not moving. This is to filter out noise.
- abs(Vy) is capped to MAX_MAGNITUDE and then "normalized" to a value between 0 and 1.
- The sign of Vy is used to determine the direction input. Vy > 0 will result in UP, Vy < 0 will result in DOWN.

TODO: account for screen rotations, which will flip the direction input.
 */
public class TiltController {
    // Minimum magnitude of angular velocity needed. Values below this are treated as zero.
    private final float MIN_FILTER = 0.02f;
    // Maximum magnitude of angular velocity considered.
    private final float MAX_MAGNITUDE = 2;

    // Current angular velocity as reported by the gyroscope.
    // See https://developer.android.com/reference/android/hardware/SensorEvent#sensor.type_gyroscope
    private float currVelocity;

    public void inputGyroscopeEvent(SensorEvent gyroEvent) {
        if (gyroEvent.sensor.getType() != Sensor.TYPE_GYROSCOPE) {
            throw new AssertionError("Only accepts GYROSCOPE events.");
        }
        currVelocity = gyroEvent.values[1];
    }

    // TODO: combine these into a single `calculateInput()` method.
    public Spaceship.Direction getDirection() {
        // TODO: a "ControlDirection" enum.
        if (currVelocity > MIN_FILTER) {
            return Spaceship.Direction.UP;
        } else if (currVelocity < -MIN_FILTER) {
            return Spaceship.Direction.DOWN;
        } else {
            return Spaceship.Direction.NONE;
        }
    }

    public float getMagnitude() {
        float magnitude = Math.abs(currVelocity);
        // Cap velocity to `MAX_MAGNITUDE`.
        magnitude = Math.min(magnitude, MAX_MAGNITUDE);
        if (magnitude < MIN_FILTER) {
            return 0;
        }
        // Normalize.
        return magnitude / MAX_MAGNITUDE;
    }
}