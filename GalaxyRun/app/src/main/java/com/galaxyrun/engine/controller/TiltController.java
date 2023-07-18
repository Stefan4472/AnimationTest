package com.galaxyrun.engine.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/*
Takes a real-time stream of gyroscope SensorEvents and turns them into control inputs for the spaceship.

Uses a simple model. Let `Vy` be the angular rotation around the y-axis, as reported by the gyroscope:
- abs(Vy) is capped to MAX_MAGNITUDE and then "normalized" to a value between 0 and 1.
- If normalized(Vy) < NOISE_THRESHOLD, we consider the phone to be not moving.
- The sign of Vy is used to determine the direction input. Vy > 0 will result in UP, Vy < 0 will result in DOWN.

TODO: account for screen rotations, which will flip the direction input.
 */
public class TiltController {
    // Minimum magnitude of angular velocity needed. Values below this are treated as noise and "clipped" to zero.
    private final float NOISE_THRESHOLD = 0.01f;
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

    public TiltState calculateState() {
        return calculateStateHelper(currVelocity);
    }

    private TiltState calculateStateHelper(float velocity) {
        // Normalize to [-1, 1]
        if (velocity > MAX_MAGNITUDE) {
            velocity = MAX_MAGNITUDE;
        } else if (velocity < -MAX_MAGNITUDE) {
            velocity = -MAX_MAGNITUDE;
        }
        velocity = velocity / MAX_MAGNITUDE;

        if (velocity > NOISE_THRESHOLD) {
            return new TiltState(ControlDirection.UP, Math.abs(velocity));
        } else if (velocity < -NOISE_THRESHOLD) {
            return new TiltState(ControlDirection.DOWN, Math.abs(velocity));
        } else {
            return new TiltState(ControlDirection.NEUTRAL, 0);
        }
    }
}