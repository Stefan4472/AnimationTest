package com.plainsimple.spaceships.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * Class to develop algorithm to parse maps
 */

public class MapParser {

    private String toParse;

    // list of GenCommands parsed from the String
    private List<GenCommand> generated = new LinkedList<>();
    // pointer to next GenCommand to be requested
    private int pointer = -1;

    // infinite keyword
    private static final String INFINITE = "INFINITE";
    private static final byte DEFAULT = 0;

    //  "loop(3, genObstacle[10])"
    public MapParser(String toParse) {
        this.toParse = toParse;
        while (!toParse.isEmpty()) {
            if (toParse.startsWith("loop(")) { // todo: nested loops
                generated.addAll(evalLoop(toParse.substring(5, toParse.indexOf(')'))));
                toParse = toParse.substring(toParse.indexOf(')') + 1);
            } else {
                generated.add(evalCommand(toParse.substring(0, toParse.indexOf(','))));
                toParse = toParse.substring(toParse.indexOf(',') + 1);
            }
        }
    }

    // takes the params of a LOOP and turns it into a list of GenCommands
    // format: number of times to loop other arguments, followed by comma-separated commandStrings
    private List<GenCommand> evalLoop(String loopStr) {
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
        List<GenCommand> commands = new LinkedList<>();
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
    private GenCommand evalCommand(String commandStr) {
        // check whether contains '[', indicating a size param
        byte size;
        if (commandStr.contains("[")) {
            size = Byte.parseByte(commandStr.substring(commandStr.indexOf('[') + 1, commandStr.indexOf(']')));
            commandStr = commandStr.substring(0, commandStr.indexOf('['));
        } else {
            size = DEFAULT;
        }
        byte type = getByteFromKeyWord(commandStr);
        return new GenCommand(type, size);
    }

    private byte getByteFromKeyWord(String keyword) {
        return 0;
    }
    
    public GenCommand next() { // todo: check indexoutofbounds?
        pointer++;
        return generated.get(pointer);
    }

    public void restart() {
        pointer = -1;
    }

    // contains information to command a generate function from TileGenerator
    private class GenCommand {
        private byte type, size;

        public GenCommand(byte type, byte size) {
            this.type = type;
            this.size = size;
        }

        public byte getType() {
            return type;
        }

        public byte getSize() {
            return size;
        }
    }
}
