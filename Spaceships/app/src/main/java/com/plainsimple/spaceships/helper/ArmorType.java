package com.plainsimple.spaceships.helper;

/**
 * Possible Armor types with their names and values
 */

public enum ArmorType {
    DEFAULT(30),
    UPGRADE_1(40),
    UPGRADE_2(50),
    UPGRADE_3(75);

    private int hp;

    ArmorType(int hp) {
        this.hp = hp;
    }

    public int getHP() {
        return hp;
    }

    @Override
    public String toString() {
        return "ArmorType." + super.toString();
    }
}
