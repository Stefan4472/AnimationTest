package com.plainsimple.spaceships.stats;

/**
 * Defines methods any class that collects/updates/stores statistics
 * should implement (if it wants to be displayed). These two methods
 * essentially facilitate easy getting of values for display in a layout.
 */

public interface StatsContainer {

    // returns keys to stats values in a String[] array that should be
    // displayed. The array should be sorted in the order the values
    // will be displayed (from top to bottom)
    String[] getKeysToDisplay();

    // returns the value of the statistic given by the key,
    // formatted for display. Throws IllegalArgumentException
    // if key is not recognized/doesn't have a value associated
    // with it.
    String getFormatted(String key) throws IllegalArgumentException;
}
