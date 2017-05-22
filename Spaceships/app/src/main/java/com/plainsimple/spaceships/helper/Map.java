package com.plainsimple.spaceships.helper;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * The Map class manages the generation of tiles. This is done with a list of GenCommands, which are
 * used by the TileGenerator methods to create byte[] arrays defining the sprites that get genCommands
 * by GameDriver. A Map is constructed by parsing from a String, which must be in the proper format.
 * The Map uses an integer pointer to keep track of where it is in its list of GenCommands, so that
 * each subsequent GenCommand simply increments the pointer and gets the next GenCommand in the list.
 * When the Map is reset, this pointer is simply reset to -1.
 *
 * Format for defining a Map:
 */

public class Map {

    // infinite keyword
    private static final String INFINITE = "INFINITE";

    // list of GenCommands parsed from the String
    private List<TileGenerator.GenCommand> genCommands = new LinkedList<>();
    // index of next GenCommand to be requested
    private int pointer = -1;
    // whether chunks will be generated endlessly
    private boolean endless;
    // index on genCommands to start looping from if endless = true
    private int endlessLoopIndex;

    private Map() {

    }

    // returns the next chunk of the Map. Does this by making a call to TileGenerator with the next
    // genCommand. Throws IndexOutOfBoundsException if there are no more chunks left to be generated.
    public byte[][] genNext(int difficulty) throws IndexOutOfBoundsException {
        pointer++;
        // if we've reached the end of genCommands and endless is true, revert to endlessLoopIndex
        if (pointer == genCommands.size() && endless) {
            pointer = endlessLoopIndex;
        } else if (pointer == genCommands.size() && !endless) { // throw exception
            throw new IndexOutOfBoundsException("Can't generate any more");
        }
        return TileGenerator.generateTiles(genCommands.get(pointer), difficulty);

    }

    public void restart() {
        pointer = -1;
    }

    public boolean isEndless() {
        return endless;
    }

    protected List<TileGenerator.GenCommand> getGenCommands() {
        return genCommands;
    }

    // adds a Map to this map. Checks to see if it is Endless, in which case it sets this one to
    // Endless and sets endlessLoopIndex to the current number of genCommands.
    // In either case the genCommands from other are added to the end of the list of genCommands
    protected void addMap(Map other) {
        if (other.isEndless()) {
            endless = true;
            endlessLoopIndex = genCommands.size();
        }
        genCommands.addAll(other.getGenCommands());
    }

    //  creates a Map object from the given String. Uses evalLoop() and evalCommand() as helpers.
    // Returns the parsed Map object
    public static Map parse(String toParse) {
        Log.d("Map", "Parsing '" + toParse + "'");
        toParse = toParse.trim();
        Map parser = new Map();
        while (!toParse.isEmpty()) {
            // evaluate loop
            if (toParse.startsWith("loop(")) { // todo: nested loops
                parser.addMap(evalLoop(toParse.substring(5, toParse.indexOf(')'))));
                toParse = toParse.substring(toParse.indexOf(')') + 1);
                // remove the comma afterward, if it exists
                if (toParse.startsWith(",")) {
                    toParse = toParse.substring(1);
                }
            } else if (toParse.contains(",")){ // at least one more command
                parser.genCommands.add(evalCommand(toParse.substring(0, toParse.indexOf(','))));
                toParse = toParse.substring(toParse.indexOf(',') + 1);
            } else { // one more command
                parser.genCommands.add(evalCommand(toParse));
                toParse = "";
            }
        }
        Log.d("Map", "Parsed to " + parser);
        return parser;
    }

    // takes the params of a loop and creates a Map object from it, which contains a list of
    // the GenCommands as well as instructions for dealing with infinite loops.
    // format: number of times to loop other arguments, followed by comma-separated commandStrings
    private static Map evalLoop(String loopStr) {
        Log.d("Map", "Evaluating loop " + loopStr);
        Map evaluated = new Map();
        // split into params
        String[] args = loopStr.split(",");
        // first param is number of times to loop sequence of following commands
        int num_loops;
        if (args[0].equals(INFINITE)) {
            evaluated.endless = true;
            evaluated.endlessLoopIndex = 0;
            num_loops = 1;
        } else {
            num_loops = Integer.parseInt(args[0]);
        }
        // evaluate each other argument as a command and add it to the list
        List<TileGenerator.GenCommand> commands = new LinkedList<>();
        for (int i = 1; i < args.length; i++) {
            commands.add(evalCommand(args[i]));
        }
        // copy over the list of commands to the evaluated Map
        for (int i = 0; i < num_loops; i++) {
            evaluated.getGenCommands().addAll(commands);
        }
        return evaluated;
    }

    // takes the String defining a genCommand and parses it into a GenCommand object, returns
    // format: key word followed by optional '[size]' param
    private static TileGenerator.GenCommand evalCommand(String commandStr) {
        Log.d("Map", "Evaluating command " + commandStr);
        // check whether contains '[', indicating a size param
        byte size;
        if (commandStr.contains("[")) {
            size = Byte.parseByte(commandStr.substring(commandStr.indexOf('[') + 1, commandStr.indexOf(']')));
            commandStr = commandStr.substring(0, commandStr.indexOf('['));
        } else {
            size = TileGenerator.GenCommand.DEFAULT;
        }
        return new TileGenerator.GenCommand(commandStr, size);
    }

    @Override
    public String toString() {
        String str = "Map: pointer at " + pointer + ". Endless = " + endless + " w/loopIndex " + endlessLoopIndex + "\n";
        for (int i = 0; i < genCommands.size(); i++) {
            str += ":" + genCommands.get(i).toString() + "\n";
        }
        return str;
    }
}
