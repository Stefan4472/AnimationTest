package com.plainsimple.spaceships.helper;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * The Map class manages the generation of tiles. This is done with a list of GenCommands, which are
 * used by the TileGenerator methods to create byte[] arrays defining the sprites that get generated
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
    private List<TileGenerator.GenCommand> generated = new LinkedList<>();
    // index of next GenCommand to be requested
    private int pointer = -1;

    private Map() {

    }

    public byte[][] genNext(int difficulty) { // todo: check indexoutofbounds?
        pointer++;
        // call TileGenerator to generate a chunk given the next GenCommand and current difficulty
        return TileGenerator.generateTiles(generated.get(pointer), difficulty);
    }

    public void restart() {
        pointer = -1;
    }

    //  "loop(3, genObstacle[10])"
    public static Map parse(String toParse) {
        Log.d("Map", "Parsing '" + toParse + "'");
        toParse = toParse.trim();
        Map parser = new Map();
        while (!toParse.isEmpty()) {
            // evaluate loop
            if (toParse.startsWith("loop(")) { // todo: nested loops
                parser.generated.addAll(evalLoop(toParse.substring(5, toParse.indexOf(')'))));
                toParse = toParse.substring(toParse.indexOf(')') + 1);
                // remove the comma afterward, if it exists
                if (toParse.startsWith(",")) {
                    toParse = toParse.substring(1);
                }
            } else if (toParse.contains(",")){ // at least one more command
                parser.generated.add(evalCommand(toParse.substring(0, toParse.indexOf(','))));
                toParse = toParse.substring(toParse.indexOf(',') + 1);
            } else { // one more command
                parser.generated.add(evalCommand(toParse));
                toParse = "";
            }
        }
        Log.d("Map", "Parsed to " + parser);
        return parser;
    }

    // takes the params of a LOOP and turns it into a list of GenCommands
    // format: number of times to loop other arguments, followed by comma-separated commandStrings
    private static List<TileGenerator.GenCommand> evalLoop(String loopStr) {
        Log.d("Map", "Evaluating loop " + loopStr);
        // split into params
        String[] args = loopStr.split(",");
        // first param is number of times to loop sequence of following commands
        int num_loops;
        if (args[0].equals(INFINITE)) {
            num_loops = 1;
            // todo: how to return infinite?
        } else {
            num_loops = Integer.parseInt(args[0]);
        }
        // evaluate each other argument as a command and add it to the list
        List<TileGenerator.GenCommand> commands = new LinkedList<>();
        for (int i = 1; i < args.length; i++) {
            commands.add(evalCommand(args[i]));
        }
        // if loops > 1, create a new list and copy over the commands num_loops times
        if (num_loops > 1) {
            List<TileGenerator.GenCommand> looped = new LinkedList<>();
            for (int i = 0; i < num_loops; i++) {
                looped.addAll(commands);
            }
            return looped;
        } else {
            return commands;
        }
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
        String str = "Map: pointer at " + pointer + "\n";
        for (int i = 0; i < generated.size(); i++) {
            str += ":" + generated.get(i).toString() + "\n";
        }
        return str;
    }
}
