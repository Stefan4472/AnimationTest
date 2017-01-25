package com.plainsimple.spaceships.helper;

/**
 * Class that stores data used to populate ListView elements in StoreActivity
 */

public class CustomItemData { // TESTING

    public int number;
    public String string;

    public CustomItemData(int number, String string) {
        this.number = number;
        this.string = string;
    }

    public int getNumber() {
        return number;
    }

    public String getString() {
        return string;
    }
}
