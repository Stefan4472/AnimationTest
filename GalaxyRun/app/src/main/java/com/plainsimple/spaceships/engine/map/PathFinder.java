package com.plainsimple.spaceships.engine.map;

import android.util.Log;

import com.plainsimple.spaceships.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/*
Find paths through a chunk.
 */
public class PathFinder {
    private static class PathNode implements Comparable<PathNode> {
        public int row, col;
        public int clearance;
        public PathNode prev;
        public PathNode(int row, int col, int clearance, PathNode prev) {
            this.row = row;
            this.col = col;
            this.clearance = clearance;
            this.prev = prev;
        }

        @Override
        public int compareTo(PathNode other) {
            // Longer path (=higher col) wins
            if (col > other.col) {
                return -1;
            } else if (col < other.col) {
                return +1;
            } else {
                return 0;
            }
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(row) + Integer.hashCode(col);
        }
    }

    public static void findPath(Chunk chunk, int maxSteps) {
        List<Pair<Integer, Integer>> possibleStarts = new ArrayList<>(6);
        for (int i = 0; i < chunk.numRows; i++) {
            if (chunk.tiles[i][0] == TileType.EMPTY) {
                possibleStarts.add(new Pair<>(i, 0));
            }
        }
        Collections.shuffle(possibleStarts);
        findPath(chunk, possibleStarts, maxSteps);
    }

    public static void findPath(Chunk chunk, List<Pair<Integer, Integer>> startLocs, int maxSteps) {
        PriorityQueue<PathNode> pathQueue = new PriorityQueue<>();

        for (Pair<Integer, Integer> start : startLocs) {
            if (isFlyable(chunk, start.first, start.second)) {
                pathQueue.add(new PathNode(start.first, start.second, 0, null));
            }
        }

        PathNode end = runDijkstra(chunk, pathQueue, maxSteps);
        if (end != null) {
            Log.d("PathFinder", "Found a path!");
            // TODO: would be cool to get a `Path` object with metadata
            List<PathNode> path = unrollPath(end);
            for (PathNode p : path) {
                Log.d("PathFinder", p.row + ", " + p.col);
            }
        }
    }

    private static PathNode runDijkstra(Chunk chunk, PriorityQueue<PathNode> pathQueue, int maxSteps) {
        HashSet<PathNode> explored = new HashSet<>();
        int numSteps = 0;

        while (!pathQueue.isEmpty() && numSteps < maxSteps) {
            numSteps++;
            PathNode next = pathQueue.poll();
            if (next == null || explored.contains(next)) {
                continue;
            }
            // TODO: is the first one found the solution?... can't remember
            if (next.col + 1 == chunk.numCols) {
                return next;
            }
            if (isFlyable(chunk, next.row, next.col + 1)) {
                pathQueue.add(new PathNode(next.row, next.col + 1, 0, next));
            }
            if (isFlyable(chunk, next.row + 1, next.col)) {
                pathQueue.add(new PathNode(next.row + 1, next.col, 0, next));
            }
            if (isFlyable(chunk, next.row - 1, next.col)) {
                pathQueue.add(new PathNode(next.row - 1, next.col, 0, next));
            }
            explored.add(next);
        }
        return null; // TODO: throw exception?
    }

    private static boolean isFlyable(Chunk chunk, int i, int j) {
        return i >= 0 && i < chunk.numRows && j >= 0 && j < chunk.numCols && chunk.tiles[i][j] == TileType.EMPTY;
    }

    private static List<PathNode> unrollPath(PathNode end) {
        List<PathNode> unrolled = new ArrayList<>(20);
        while (end != null) {
            unrolled.add(end);
            end = end.prev;
        }
        Collections.reverse(unrolled);
        return unrolled;
    }
}
