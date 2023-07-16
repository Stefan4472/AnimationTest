package com.galaxyrun.engine.controller;

import com.galaxyrun.sprite.Spaceship;

public class ControlState {
    // Direction the spaceship should fly in.
    public final Spaceship.Direction direction;
    // Magnitude of the direction change. Must be between [0, 1].
    public final float magnitude;
    // Whether the spaceships should shoot.
    public final boolean isShooting;

    public ControlState(Spaceship.Direction direction, float magnitude, boolean isShooting) {
        if (magnitude < 0 || magnitude > 1) {
            throw new AssertionError("Invalid magnitude " + magnitude);
        }
        this.direction = direction;
        this.magnitude = magnitude;
        this.isShooting = isShooting;
    }
}
