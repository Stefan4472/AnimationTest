package com.plainsimple.spaceships.store;

/**
 * Possible Armor types with their names and values
 */

public enum ArmorType {
    ARMOR_0(30),
    ARMOR_1(40),
    ARMOR_2(60),
    ARMOR_3(85);

    private int hp;

    ArmorType(int hp) {
        this.hp = hp;
    }

    public int getHP() {
        return hp;
    }
}
