package com.plainsimple.spaceships.helper;

import com.plainsimple.spaceships.activity.GameActivity;
import com.plainsimple.spaceships.view.GameView;

import java.util.IllegalFormatCodePointException;

/**
 * Stores all information required to administer a specific GameMode. GameModes are written for easy
 * persistent storage and retrieval via the toString/fromString methods.
 */

public class GameMode {

    // GameMode's name (e.g. "Asteroid Survival"
    private String name;
    // level of difficulty GameMode was last played on
    private GameView.Difficulty lastDifficulty;
    // highest score achieved by the player
    private int highscore;
    // points required for 1, 2, 3, 4, and 5 stars respectively
    private int oneStarPoints, twoStarPoints, threeStarPoints, fourStarPoints, fiveStarPoints;
    // game instructions
    private String instructions;
    // data used by the game driver to build the level
    private String levelData;

    private GameMode() { // todo: refinements?

    }

    @Override
    public String toString() {
        return name + ":" + lastDifficulty.toString() + ":" + highscore + ":" + oneStarPoints +
                ":" + twoStarPoints + ":" + threeStarPoints + ":" + fourStarPoints + ":" +
                fiveStarPoints + ":" + instructions + ":" + levelData;
    }

    public static String toString(GameMode gameMode) {
        return gameMode.toString();
    }

    // creates a GameMode object from the given String. Only works if the String was generated
    // by a GameMode's toString() method (requires specific format!). Throws IllegalArgumentException
    // if given String cannot be parsed.
    public static GameMode fromString(String constructor) throws IllegalArgumentException {
        String[] args = constructor.split(":");
        GameMode initialized = new GameMode();
        try {
            initialized.name = args[0];
            initialized.lastDifficulty = GameView.Difficulty.valueOf(args[1]);
            initialized.highscore = Integer.parseInt(args[2]);
            initialized.oneStarPoints = Integer.parseInt(args[3]);
            initialized.twoStarPoints = Integer.parseInt(args[4]);
            initialized.threeStarPoints = Integer.parseInt(args[5]);
            initialized.fourStarPoints = Integer.parseInt(args[6]);
            initialized.fiveStarPoints = Integer.parseInt(args[7]);
            initialized.instructions = args[8];
            initialized.levelData = args[9];
            return initialized;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing the given String. It should be " +
                    "the toString() of a GameMode object");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Not enough parameters");
        }
    }

    public String getName() {
        return name;
    }

    public GameView.Difficulty getLastDifficulty() {
        return lastDifficulty;
    }

    public void setLastDifficulty(GameView.Difficulty lastDifficulty) {
        this.lastDifficulty = lastDifficulty;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public int getOneStarPoints() {
        return oneStarPoints;
    }

    public int getTwoStarPoints() {
        return twoStarPoints;
    }

    public int getThreeStarPoints() {
        return threeStarPoints;
    }

    public int getFourStarPoints() {
        return fourStarPoints;
    }

    public int getFiveStarPoints() {
        return fiveStarPoints;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getLevelData() {
        return levelData;
    }
}
