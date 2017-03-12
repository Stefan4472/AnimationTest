package com.plainsimple.spaceships.helper;

/**
 * Possible Armor types with their names and values
 */

public enum ArmorType {
    ARMOR_0(30),
    ARMOR_1(40),
    ARMOR_2(50),
    ARMOR_3(75);

    private int hp;

    ArmorType(int hp) {
        this.hp = hp;
    }

    public int getHP() {
        return hp;
    }
}
