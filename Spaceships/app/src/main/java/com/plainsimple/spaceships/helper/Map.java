package com.plainsimple.spaceships.helper;

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
//        return TileGenerator.generateTiles(generated.get(pointer));
        return null;
    }

    public void restart() {
        pointer = -1;
    }

    //  "loop(3, genObstacle[10])"
    public static Map parse(String toParse) {
        Map parser = new Map();
        while (!toParse.isEmpty()) {
            if (toParse.startsWith("loop(")) { // todo: nested loops
                parser.generated.addAll(evalLoop(toParse.substring(5, toParse.indexOf(')'))));
                toParse = toParse.substring(toParse.indexOf(')') + 1);
            } else {
                parser.generated.add(evalCommand(toParse.substring(0, toParse.indexOf(','))));
                toParse = toParse.substring(toParse.indexOf(',') + 1);
            }
        }
        return parser;
    }

    // takes the params of a LOOP and turns it into a list of GenCommands
    // format: number of times to loop other arguments, followed by comma-separated commandStrings
    private static List<TileGenerator.GenCommand> evalLoop(String loopStr) {
        // split into params
        String[] args = loopStr.split(",");
        // first param is number of times to loop sequence of following commands
        int loops;
        if (args[0].equals(INFINITE)) {
            loops = 1;
            // todo: how to return infinite?
        } else {
            loops = Integer.parseInt(args[0]);
        }
        List<TileGenerator.GenCommand> commands = new LinkedList<>();
        for (int i = 1; i < args.length; i++) {
            commands.add(evalCommand(args[i]));
        }
        // copy LOOP number of times
        for (int i = 0; i < loops; i++) {
            commands.addAll(commands);
        }
        return commands;
    }

    // takes the String defining a genCommand and parses it into a GenCommand object, returns
    // format: key word followed by optional '[size]' param
    private static TileGenerator.GenCommand evalCommand(String commandStr) {
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
}
